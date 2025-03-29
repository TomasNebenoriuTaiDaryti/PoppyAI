package com.example.poppyai.controllers;

import com.example.poppyai.dto.OmdbMovieResponse;
import com.example.poppyai.service.OmdbService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final OmdbService omdbService;

    public MovieController(OmdbService omdbService) {
        this.omdbService = omdbService;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/search")
    public OmdbMovieResponse getMovieInfo(@RequestParam String title) {
        return omdbService.fetchMovieByTitle(title);
    }
}
