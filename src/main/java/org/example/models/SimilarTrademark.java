package org.example.models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "SimilarTrademarks")
@Getter
@Setter
public class SimilarTrademark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int searchId;

    @Column(nullable = false)
    private int trademarkId;

    @Column
    private byte[] image;

}