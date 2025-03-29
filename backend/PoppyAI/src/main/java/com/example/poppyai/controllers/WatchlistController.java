package com.example.poppyai.controllers;

import com.example.poppyai.model.Movie;
import com.example.poppyai.model.User;
import com.example.poppyai.model.WatchList;
import com.example.poppyai.repo.MovieRepo;
import com.example.poppyai.repo.UserRepo;
import com.example.poppyai.repo.WatchListRepo;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.misc.LogManager;
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

    @Autowired
    private MovieRepo movieRepo;

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

    @PostMapping("/watchlist/{username}/add")
    public ResponseEntity<?> addToWatchlist(
            @PathVariable String username,
            @RequestBody Movie movieRequest
    ) {
        try {
            User user = userRepo.findByUsername(username);
            if (user == null) return ResponseEntity.notFound().build();

            Movie movie = movieRepo.findByImdbID(movieRequest.getImdbID());
            if (movie == null) {
                movie = new Movie(
                        movieRequest.getTitle(),
                        movieRequest.getGenre(),
                        movieRequest.getDescription(),
                        movieRequest.getRating(),
                        movieRequest.getImdbID()
                );
                movieRepo.save(movie);
            }

            if (!watchListRepo.existsByUserAndMovie(user, movie)) {
                WatchList entry = new WatchList(user, movie);
                watchListRepo.save(entry);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error adding to watchlist");
        }
    }
}