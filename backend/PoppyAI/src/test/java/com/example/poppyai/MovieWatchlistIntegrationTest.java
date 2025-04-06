package com.example.poppyai;

import com.example.poppyai.dto.OmdbMovieResponse;
import com.example.poppyai.model.User;
import com.example.poppyai.repo.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MovieWatchlistIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper mapper;

    private static final String TEST_USERNAME = "watchlistUser";
    private static final String TEST_PASSWORD = "pass123";
    private static final String TEST_EMAIL = "watchlist@example.com";
    private static final String MOVIE_TITLE = "Inception";

    private static String imdbId;

    @BeforeEach
    public void setupUser() {
        if (userRepo.findByUsername(TEST_USERNAME) == null) {
            String hashed = passwordEncoder.encode(TEST_PASSWORD);
            userRepo.save(new User(TEST_USERNAME, hashed, TEST_EMAIL));
        }
    }

    @Test
    @Order(1)
    public void testMovieSearch_andExtractImdbId() throws Exception {
        var response = mockMvc.perform(get("/api/movies/search")
                        .param("title", MOVIE_TITLE))
                .andExpect(status().isOk())
                .andReturn();

        OmdbMovieResponse movie = mapper.readValue(
                response.getResponse().getContentAsString(),
                OmdbMovieResponse.class
        );

        assertNotNull(movie);
        assertNotNull(movie.getImdbID());
        assertEquals(MOVIE_TITLE, movie.getTitle());

        imdbId = movie.getImdbID();
    }

    @Test
    @Order(2)
    public void testAddAndVerifyMovieInWatchlist() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"" + TEST_USERNAME + "\", \"password\": \"" + TEST_PASSWORD + "\"}"))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/movies/search")
                        .param("title", MOVIE_TITLE))
                .andExpect(status().isOk())
                .andReturn();

        OmdbMovieResponse movie = mapper.readValue(
                result.getResponse().getContentAsString(),
                OmdbMovieResponse.class
        );

        assertNotNull(movie.getImdbID(), "IMDb ID must not be null");
        imdbId = movie.getImdbID();

        mockMvc.perform(post("/api/watchlist/" + TEST_USERNAME + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movie)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/watchlist/" + TEST_USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(imdbId)));
    }

}
