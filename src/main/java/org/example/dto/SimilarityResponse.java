package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarityResponse {
    private int trademarkId;
    private double similarityScore;
}