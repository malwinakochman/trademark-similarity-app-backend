package org.example.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.SimilarityResponse;
import org.example.models.SimilarTrademark;
import org.example.models.Trademark;
import org.example.services.SimilarTrademarkService;
import org.example.services.TrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class SimilarityController {

    private final SimilarTrademarkService similarTrademarkService;
    private final TrademarkService trademarkService;
    private static final Logger logger = LoggerFactory.getLogger(SimilarityController.class);

    @Autowired
    public SimilarityController(SimilarTrademarkService similarTrademarkService, TrademarkService trademarkService) {
        this.similarTrademarkService = similarTrademarkService;
        this.trademarkService = trademarkService;
    }

    @GetMapping("/check-similarity/{id}")
    public ResponseEntity<?> checkSimilarity(@PathVariable String id) {
        try {
            String pythonInterpreterPath = "myenv/bin/python3.11";
            ProcessBuilder processBuilder = new ProcessBuilder(pythonInterpreterPath,
                    "src/main/java/org/example/helpers/pythonScript/similarityAppFromDb.py",
                    "--id", id, "--silent");

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                ObjectMapper mapper = new ObjectMapper();
                List<SimilarityResponse> trademarks = mapper.readValue(
                        output.toString(), new TypeReference<List<SimilarityResponse>>() {}
                );


                for (SimilarityResponse trademark : trademarks) {
                    SimilarTrademark similarTrademark = new SimilarTrademark();
                    similarTrademark.setSearchId(Integer.parseInt(id));
                    int trademarkId = trademark.getTrademarkId();
                    similarTrademark.setTrademarkId(trademarkId);
                    Optional<Trademark> trademarkFromDb = trademarkService.getTrademarkById(trademarkId);
                    byte[] image = trademarkFromDb.map(Trademark::getImage).orElse(null);
                    similarTrademark.setImage(image);
                    addTrademark(similarTrademark);
                }

                return new ResponseEntity<>(trademarks, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Error executing Python script. Exit code: " + exitCode, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public SimilarTrademark addTrademark(@RequestBody SimilarTrademark trademark) {
        return similarTrademarkService.addTrademark(trademark);
    }

    @GetMapping("/get-similar/{id}")
    public List<SimilarTrademark> getAllSimilar(@PathVariable String id) {
        return similarTrademarkService.getAllBySearchId(id);
    }

}