package org.example.services;

import org.example.models.SimilarTrademark;
import org.example.repositories.SimilarTrademarkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimilarTrademarkService {

    private final SimilarTrademarkRepository similarTrademarkRepository;

    public SimilarTrademarkService(SimilarTrademarkRepository similarTrademarkRepository) {
        this.similarTrademarkRepository = similarTrademarkRepository;
    }

    public SimilarTrademark addTrademark(SimilarTrademark trademark) {
        return similarTrademarkRepository.save(trademark);
    }

    public List<SimilarTrademark> getAllBySearchId(String id) {
        int searchId = Integer.parseInt(id);
        return similarTrademarkRepository.getBySearchId(searchId);
    }

}
