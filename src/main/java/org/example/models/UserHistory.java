package org.example.models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "UsersHistory")
@Getter
@Setter
public class UserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int searchId;

    @Column(nullable = false)
    private int userId;

    @Lob
    private byte[] comparedImage;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private String fileName;
}
