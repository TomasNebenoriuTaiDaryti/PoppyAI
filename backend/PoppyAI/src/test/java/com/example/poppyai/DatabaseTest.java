package com.example.poppyai;
import com.example.poppyai.model.Movie;
import com.example.poppyai.model.User;
import com.example.poppyai.model.WatchList;
import com.example.poppyai.repo.MovieRepo;
import com.example.poppyai.repo.UserRepo;
import com.example.poppyai.repo.WatchListRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DatabaseTest {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MovieRepo movieRepo;

    @Autowired
    private WatchListRepo watchListRepo;

    @Test
    public void testAddWatchListEntry() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user = userRepo.save(user);

        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setGenre("Sci-Fi");
        movie.setDescription("A mind-bending thriller");
        movie = movieRepo.save(movie);

        WatchList watchListEntry = new WatchList(user, movie);
        watchListEntry = watchListRepo.save(watchListEntry);

        assertNotNull(watchListEntry.getId(), "Egzistuos watchlist");
        assertEquals(user.getId(), watchListEntry.getUser().getId(), "egzistuos toks useris, kuris buvo ikeltas i db");
        assertEquals(movie.getId(), watchListEntry.getMovie().getId(), "egzistuos toks filmas, kuris buvo ikeltas i db");
    }

    @Test
    public void testRemoveWatchListEntry() {
        User user = new User();
        user.setUsername("testUser2");
        user.setPassword("password");
        user.setEmail("test2@example.com");
        user = userRepo.save(user);

        Movie movie = new Movie();
        movie.setTitle("Interstellar");
        movie.setGenre("Sci-Fi");
        movie.setDescription("A space epic");
        movie = movieRepo.save(movie);

        WatchList watchListEntry = new WatchList(user, movie);
        watchListEntry = watchListRepo.save(watchListEntry);

        watchListRepo.delete(watchListEntry);

        Optional<WatchList> deletedEntry = watchListRepo.findById(watchListEntry.getId());
        assertFalse(deletedEntry.isPresent(), "watchlistas turetu buti istrintas");
    }

    @Test
    public void testUpdateWatchListEntry() {
        User user = new User();
        user.setUsername("updateUser");
        user.setPassword("password");
        user.setEmail("update@example.com");
        user = userRepo.save(user);

        Movie movie1 = new Movie();
        movie1.setTitle("The Matrix");
        movie1.setGenre("Sci-Fi");
        movie1.setDescription("A computer hacker learns the truth about his reality.");
        movie1 = movieRepo.save(movie1);

        Movie movie2 = new Movie();
        movie2.setTitle("The Matrix Reloaded");
        movie2.setGenre("Sci-Fi");
        movie2.setDescription("The sequel to The Matrix.");
        movie2 = movieRepo.save(movie2);

        WatchList watchListEntry = new WatchList(user, movie1);
        watchListEntry = watchListRepo.save(watchListEntry);

        WatchList entryToUpdate = watchListRepo.findById(watchListEntry.getId()).orElseThrow();
        entryToUpdate.setMovie(movie2);
        watchListRepo.save(entryToUpdate);

        WatchList updatedEntry = watchListRepo.findById(watchListEntry.getId()).orElseThrow();
        assertEquals(movie2.getId(), updatedEntry.getMovie().getId(), "Atnaujintas watchlist turi tureti antra filma");
    }
}
