package com.example.poppyai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String title;
    private String genre;
    private String description;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<WatchList> watchlist = new ArrayList<>();;

    public Movie(String title, String genre, String description) {
        this.title = title;
        this.genre = genre;
        this.description = description;
    }
}
