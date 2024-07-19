package org.example.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageUtils {

    public static void decodeBase64AndSaveToFile(String base64String, String filePath) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(imageBytes);
        }
    }
}
