package com.example.poppyai.controllers;

import com.example.poppyai.model.Movie;
import com.example.poppyai.model.User;
import com.example.poppyai.model.WatchList;
import com.example.poppyai.repo.UserRepo;
import com.example.poppyai.repo.WatchListRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class WatchlistController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private WatchListRepo watchListRepo;

    @GetMapping("/watchlist/{username}")
    public ResponseEntity<?> getWatchlist(@PathVariable String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<WatchList> watchLists = watchListRepo.findByUserWithMovies(user);
        List<Movie> movies = watchLists.stream().map(WatchList::getMovie).collect(Collectors.toList());
        return ResponseEntity.ok(movies);
    }
    @Transactional
    @DeleteMapping("/watchlist/{username}/{movieId}")
    public ResponseEntity<?> removeFromWatchlist(
            @PathVariable String username,
            @PathVariable Integer movieId
    ) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        watchListRepo.deleteByUserAndMovie_Id(user, movieId);
        return ResponseEntity.ok().build();
    }
}