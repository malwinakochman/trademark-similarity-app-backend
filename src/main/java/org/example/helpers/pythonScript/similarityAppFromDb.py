import os
import sys
import pandas as pd
from tqdm import tqdm
import numpy as np
from torchvision import transforms
import torch
from torch.autograd import Variable
import timm
import faiss
import requests
from io import BytesIO
from PIL import Image
import base64
import json
import argparse

class Load_Data:
    """Klasa do ładowania danych z API"""

    def __init__(self):
        pass

    def from_api(self, user_image_url: str, trademarks_url: str):
        user_image_response = requests.get(user_image_url)
        user_image_response.raise_for_status()
        user_image = user_image_response.content

        trademarks_response = requests.get(trademarks_url)
        trademarks_response.raise_for_status()
        trademarks = trademarks_response.json()
        return user_image, trademarks

class Search_Setup:
    """Klasa do konfiguracji i uruchamiania wyszukiwania podobieństwa obrazów."""

    def __init__(self, user_image: bytes, trademarks: list, model_name='vgg19', pretrained=True, silent=False):
        self.model_name = model_name
        self.pretrained = pretrained
        self.user_image = user_image
        self.trademarks = trademarks
        self.image_data = pd.DataFrame()
        self.d = None
        self.silent = silent

        if f'metadata-files/{self.model_name}' not in os.listdir():
            try:
                os.makedirs(f'metadata-files/{self.model_name}')
            except Exception as e:
                pass

        base_model = timm.create_model(self.model_name, pretrained=self.pretrained)
        self.model = torch.nn.Sequential(*list(base_model.children())[:-1])
        self.model.eval()

    def extract(self, image_data: bytes):
        img = Image.open(BytesIO(image_data)).convert('RGB')
        if img is None:
            raise ValueError("Nie można odczytać obrazu")

        img = img.resize((224, 224))

        preprocess = transforms.Compose([
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
        ])
        x = preprocess(img)
        x = Variable(torch.unsqueeze(x, dim=0).float(), requires_grad=False)

        feature = self.model(x)
        feature = feature.data.numpy().flatten()

        return feature / np.linalg.norm(feature)

    def _get_feature(self, image_data: list):
        features = []
        for idx, img_data in enumerate(tqdm(image_data, disable=self.silent)):
            try:
                feature = self.extract(img_data)
                features.append(feature)
            except Exception as e:
                features.append(None)
                continue
        return features

    def start_feature_extraction(self):
        image_data = pd.DataFrame(self.trademarks)
        f_data = self._get_feature([base64.b64decode(item['image']) for item in self.trademarks])
        image_data['features'] = f_data
        image_data = image_data.dropna().reset_index(drop=True)
        image_data.to_pickle(image_data_with_features_pkl(self.model_name))
        return image_data

    def start_indexing(self, image_data):
        self.image_data = image_data
        d = len(image_data['features'][0])
        self.d = d
        index = faiss.IndexFlatL2(d)
        features_matrix = np.vstack(image_data['features'].values).astype(np.float32)
        index.add(features_matrix)
        faiss.write_index(index, image_features_vectors_idx(self.model_name))

    def run_index(self):
        if len(os.listdir(f'metadata-files/{self.model_name}')) == 0:
            data = self.start_feature_extraction()
            self.start_indexing(data)
        else:
            data = self.start_feature_extraction()
            self.start_indexing(data)
            self.image_data = pd.read_pickle(image_data_with_features_pkl(self.model_name))
            self.f = len(self.image_data['features'][0])

    def add_images_to_index(self, new_image_data: list):
        self.image_data = pd.read_pickle(image_data_with_features_pkl(self.model_name))
        index = faiss.read_index(image_features_vectors_idx(self.model_name))

        for new_image in tqdm(new_image_data, disable=self.silent):
            try:
                feature = self.extract(base64.b64decode(new_image['image']))
            except Exception as e:
                if not self.silent:
                    print(f"Błąd podczas ekstrakcji cech z nowego obrazu: {e}", file=sys.stderr)
                continue

            new_metadata = pd.DataFrame({"images_paths": [new_image['image']], "features": [feature]})
            self.image_data = pd.concat([self.image_data, new_metadata], axis=0, ignore_index=True)
            index.add(np.array([feature], dtype=np.float32))

        self.image_data.to_pickle(image_data_with_features_pkl(self.model_name))
        faiss.write_index(index, image_features_vectors_idx(self.model_name))

    def _search_by_vector(self, v, n: int):
        self.v = v
        self.n = n
        index = faiss.read_index(image_features_vectors_idx(self.model_name))

        D, I = index.search(np.array([self.v], dtype=np.float32), self.n * 2)

        filtered_results = [(i, d) for i, d in zip(I[0], D[0]) if d <= 1.0]

        filtered_results.sort(key=lambda x: x[1])
        filtered_results = filtered_results[:self.n]

        return dict(zip([i for i, _ in filtered_results],
                        zip(self.image_data.iloc[[i for i, _ in filtered_results]]['trademarkId'].to_list(),
                            [d for _, d in filtered_results])))

    def _get_query_vector(self, image_data: bytes):
        query_vector = self.extract(image_data)
        return query_vector

    def get_similar_images(self, user_image: bytes, number_of_images: int = 10):
        query_vector = self._get_query_vector(user_image)
        img_dict = self._search_by_vector(query_vector, number_of_images)
        return [(trademark_id, similarity) for trademark_id, similarity in img_dict.values()]

os.environ["KMP_DUPLICATE_LIB_OK"] = "TRUE"

def image_data_with_features_pkl(model_name):
    return os.path.join('metadata-files/', f'{model_name}/', 'image_data_features.pkl')

def image_features_vectors_idx(model_name):
    return os.path.join('metadata-files/', f'{model_name}/', 'image_features_vectors.idx')

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('--id', required=True, help='User trademark ID')
    parser.add_argument('--silent', action='store_true', help='Disable progress bar output')
    args = parser.parse_args()

    user_image_url = f"http://ec2-52-12-34-56.compute-1.amazonaws.com:8080/api/userTrademarks/{args.id}/image"
    trademarks_url = "http://ec2-52-12-34-56.compute-1.amazonaws.com:8080/trademarks"

    loader = Load_Data()
    user_image, trademarks = loader.from_api(user_image_url, trademarks_url)

    st = Search_Setup(user_image, trademarks, 'vgg19', True, args.silent)
    st.run_index()
    similar_images = st.get_similar_images(user_image)

    response = [
        {
            "trademarkId": trademark_id,
            "similarityScore": float(similarity)
        }
        for trademark_id, similarity in similar_images
    ]

    json_response = json.dumps(response, indent=2)
    print(json_response)