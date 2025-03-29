package com.example.poppyai;

import com.example.poppyai.model.Movie;
import com.example.poppyai.model.User;
import com.example.poppyai.model.WatchList;
import com.example.poppyai.repo.MovieRepo;
import com.example.poppyai.repo.UserRepo;
import com.example.poppyai.repo.WatchListRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class WatchlistTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MovieRepo movieRepo;

    @Autowired
    private WatchListRepo watchListRepo;

    private final String TEST_USERNAME = "watchlistUser";
    private final String TEST_MOVIE1 = "Inception";
    private final String TEST_MOVIE2 = "Interstellar";

    @BeforeEach
    public void setup() {
        User user = new User(TEST_USERNAME, "password", "watchlist@test.com");
        userRepo.save(user);

        Movie movie1 = movieRepo.save(new Movie(TEST_MOVIE1, "Sci-Fi", "Dream within a dream"));
        Movie movie2 = movieRepo.save(new Movie(TEST_MOVIE2, "Sci-Fi", "Space exploration"));

        watchListRepo.save(new WatchList(user, movie1));
        watchListRepo.save(new WatchList(user, movie2));
    }

    @Test
    public void testGetWatchlist_ValidUser_ReturnsMovies() throws Exception {
        mockMvc.perform(get("/api/watchlist/" + TEST_USERNAME).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2)).andExpect(jsonPath("$[0].title").value(TEST_MOVIE1)).andExpect(jsonPath("$[1].title").value(TEST_MOVIE2));
    }

    @Test
    public void testGetWatchlist_InvalidUser_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/watchlist/nonExistentUser").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void testWatchlistContainsCorrectMovies() throws Exception {
        String response = mockMvc.perform(get("/api/watchlist/" + TEST_USERNAME)).andReturn().getResponse().getContentAsString();

        assertTrue(response.contains(TEST_MOVIE1));
        assertTrue(response.contains(TEST_MOVIE2));
    }

    @Test
    public void testDeleteFromWatchlist() throws Exception {
        Movie movieToDelete = movieRepo.findByTitle(TEST_MOVIE1);
        mockMvc.perform(delete("/api/watchlist/" + TEST_USERNAME + "/" + movieToDelete.getId())).andExpect(status().isOk());

        List<WatchList> updatedWatchlist = watchListRepo.findByUserWithMovies(userRepo.findByUsername(TEST_USERNAME));

        boolean movie1Present = updatedWatchlist.stream().anyMatch(wl -> wl.getMovie().getTitle().equals(TEST_MOVIE1));
        boolean movie2Present = updatedWatchlist.stream().anyMatch(wl -> wl.getMovie().getTitle().equals(TEST_MOVIE2));

        assertFalse(movie1Present, "1 filmas turi buti istrintas");
        assertTrue(movie2Present, "2 filmas liks watchliste");
    }
}