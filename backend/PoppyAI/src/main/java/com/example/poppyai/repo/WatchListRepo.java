package com.example.poppyai.repo;

import com.example.poppyai.model.User;
import com.example.poppyai.model.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WatchListRepo extends JpaRepository<WatchList, Integer> {
    @Query("SELECT wl FROM WatchList wl JOIN FETCH wl.movie WHERE wl.user = :user")
    List<WatchList> findByUserWithMovies(@Param("user") User user);

    void deleteByUserAndMovie_Id(User user, Integer movie_id);
}