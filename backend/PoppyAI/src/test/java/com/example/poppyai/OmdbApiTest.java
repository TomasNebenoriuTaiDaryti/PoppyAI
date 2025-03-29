package com.example.poppyai;

import com.example.poppyai.dto.OmdbMovieResponse;
import com.example.poppyai.service.OmdbService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OmdbApiTest {

    @Autowired
    private OmdbService omdbService;

    private final String[] movieTitles = {
            "Inception", "The Matrix", "The Godfather", "The Dark Knight", "Interstellar",
            "Fight Club", "Pulp Fiction", "Forrest Gump", "The Shawshank Redemption", "Gladiator",
            "Titanic", "The Lord of the Rings: The Fellowship of the Ring", "The Avengers", "Avatar",
            "The Lion King", "Star Wars", "Joker", "Iron Man", "Deadpool", "Black Panther"
    };

    @Test
    public void testFetchMultipleMovies() {
        for (String title : movieTitles) {
            OmdbMovieResponse response = omdbService.fetchMovieByTitle(title);
            assertNotNull(response, "Response for '" + title + "' should not be null");
            assertEqualsIgnoreCase(title, response.getTitle(), "Title mismatch for '" + title + "'");
            assertNotNull(response.getGenre(), "Genre should not be null for '" + title + "'");
            assertNotNull(response.getPlot(), "Plot should not be null for '" + title + "'");
        }
    }

    private void assertEqualsIgnoreCase(String expected, String actual, String message) {
        assertNotNull(actual, "Actual title is null");
        assertTrue(actual.toLowerCase().contains(expected.toLowerCase()), message + " — expected: " + expected + ", got: " + actual);
    }
}
