package org.example.controllers;

import org.example.models.Trademark;
import org.example.services.TrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/trademarks")
public class TrademarkController {

    private final TrademarkService trademarkService;

    @Autowired
    public TrademarkController(TrademarkService trademarkService) {
        this.trademarkService = trademarkService;
    }

    @GetMapping
    public List<Trademark> getAllTrademarks() {
        return trademarkService.getAllTrademarks();
    }

    @PostMapping
    public Trademark addTrademark(@RequestBody Trademark trademark) {
        return trademarkService.addTrademark(trademark);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
        Optional<Trademark> trademark = trademarkService.getTrademarkById(id);
        if (trademark.isPresent()) {
            byte[] imageBytes = trademark.get().getImage();
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
