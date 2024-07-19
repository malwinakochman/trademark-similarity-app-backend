package org.example.models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "Trademarks")
@Getter
@Setter
public class Trademark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int trademarkId;

    @Column(nullable = false)
    private String applicationNumber;

    @Column(nullable = false)
    private String companyName;

    @Lob
    private byte[] image;
}