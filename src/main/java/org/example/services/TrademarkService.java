package org.example.services;

import org.example.models.Trademark;
import org.example.repositories.TrademarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrademarkService {

    private final TrademarkRepository trademarkRepository;

    @Autowired
    public TrademarkService(TrademarkRepository trademarkRepository) {
        this.trademarkRepository = trademarkRepository;
    }

    public List<Trademark> getAllTrademarks() {
        return trademarkRepository.findAll();
    }

    public Trademark addTrademark(Trademark trademark) {
        return trademarkRepository.save(trademark);
    }

    public Optional<Trademark> getTrademarkById(int trademarkId) {
        return trademarkRepository.findById(trademarkId);
    }
}

