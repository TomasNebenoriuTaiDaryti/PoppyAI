package com.example.poppyai.repo;

import com.example.poppyai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    public User getUserByUsernameAndPassword(String username, String password);
    public User findByUsername(String username);
}
