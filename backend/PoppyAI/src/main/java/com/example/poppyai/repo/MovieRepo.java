package com.example.poppyai.repo;

import com.example.poppyai.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepo extends JpaRepository<Movie, Long> {

}
