package com.example.poppyai.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private double rating;
    private String imdbID;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<WatchList> watchlist = new ArrayList<>();;

    public Movie(String title, String genre, String description, double rating, String imdbID) {
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.rating = rating;
        this.imdbID = imdbID;
    }
}
