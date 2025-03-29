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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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

        Movie movie1 = movieRepo.save(new Movie(TEST_MOVIE1, "Sci-Fi", "Dream within a dream", 1, "1"));
        Movie movie2 = movieRepo.save(new Movie(TEST_MOVIE2, "Sci-Fi", "Space exploration",1, "3"));

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

    @Test
    public void testAddToWatchlist() throws Exception {
        Movie newMovie = new Movie("The Matrix", "Sci-Fi", "Virtual reality adventure", 8.7, "tt0133093");

        String movieJson = """
        {
            "title": "The Matrix",
            "genre": "Sci-Fi",
            "description": "Virtual reality adventure",
            "rating": 8.7,
            "imdbID": "tt0133093"
        }
        """;

        mockMvc.perform(post("/api/watchlist/" + TEST_USERNAME + "/add").contentType(MediaType.APPLICATION_JSON).content(movieJson)).andExpect(status().isOk());

        List<WatchList> watchlist = watchListRepo.findByUserWithMovies(userRepo.findByUsername(TEST_USERNAME));

        boolean matrixAdded = watchlist.stream().anyMatch(wl -> wl.getMovie().getTitle().equals("The Matrix"));assertTrue(matrixAdded, "Naujas filmas bus idetas i watchlista");

        mockMvc.perform(post("/api/watchlist/" + TEST_USERNAME + "/add").contentType(MediaType.APPLICATION_JSON).content(movieJson)).andExpect(status().isOk());

        long matrixCount = watchlist.stream().filter(wl -> wl.getMovie().getTitle().equals("The Matrix")).count();assertEquals(1, matrixCount, "Bus tik toks vienas filmas");
    }

    @Test
    public void testAddToWatchlist_InvalidUser() throws Exception {
        String movieJson = """
        {
            "title": "Invalid User Test",
            "genre": "Test",
            "description": "Test movie",
            "rating": 5.0,
            "imdbID": "tt0000000"
        }
        """;

        mockMvc.perform(post("/api/watchlist/nonExistentUser/add").contentType(MediaType.APPLICATION_JSON).content(movieJson)).andExpect(status().isNotFound());
    }
}