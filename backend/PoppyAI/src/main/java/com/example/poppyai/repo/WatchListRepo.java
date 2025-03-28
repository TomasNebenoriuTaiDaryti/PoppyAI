package com.example.poppyai.repo;

import com.example.poppyai.model.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchListRepo extends JpaRepository<WatchList, Long> {

}
