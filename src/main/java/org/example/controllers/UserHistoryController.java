package org.example.controllers;

import jakarta.transaction.Transactional;
import org.example.helpers.ImageUtils;
import org.example.models.UserHistory;
import org.example.repositories.UserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/userTrademarks")
public class UserHistoryController {

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    @PostMapping("/add")
    public ResponseEntity<UserHistory> addUserHistory(@RequestBody UserHistory userHistory) {
        userHistory.setCreatedAt(Timestamp.from(Instant.now()));
        UserHistory savedUserHistory = userHistoryRepository.save(userHistory);
        return new ResponseEntity<>(savedUserHistory, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    @Transactional
    public ResponseEntity<List<UserHistory>> getUserHistory(@PathVariable int userId) {
        List<UserHistory> userHistory = userHistoryRepository.findByUserId(userId);
        if (userHistory != null) {
            return ResponseEntity.ok(userHistory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{searchId}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer searchId) {
        Optional<UserHistory> userHistoryOptional = userHistoryRepository.findById(searchId);
        if (userHistoryOptional.isPresent()) {
            byte[] imageBytes = userHistoryOptional.get().getComparedImage();
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
