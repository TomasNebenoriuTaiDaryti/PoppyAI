package com.example.poppyai.service;

import com.example.poppyai.dto.OmdbMovieResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OmdbService {

    @Value("${omdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public OmdbMovieResponse fetchMovieByTitle(String title) {
        String url = "http://www.omdbapi.com/?t=" + title + "&apikey=" + apiKey;

        OmdbMovieResponse response = restTemplate.getForObject(url, OmdbMovieResponse.class);

        //FOR CONSOLE DEBUG
        //System.out.println("OMDb URL: " + url);
        //System.out.println("OMDb Title: " + response.getTitle());
        //System.out.println("Genre: " + response.getGenre());
        //System.out.println("Plot: " + response.getPlot());
        //System.out.println("IMDb Link: https://www.imdb.com/title/" + response.getImdbID());

        return response;
    }


}
