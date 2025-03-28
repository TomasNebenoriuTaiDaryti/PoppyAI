package com.example.poppyai.controllers;

import com.example.poppyai.PasswordService;
import com.example.poppyai.model.User;
import com.example.poppyai.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordService passwordService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(passwordService.hashPassword(user.getPassword()));
        userRepository.save(user);
        return "Sekminga registracija!";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        Optional<User> foundUser = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));
        if (foundUser.isPresent() && passwordService.checkPassword(user.getPassword(), foundUser.get().getPassword())) {
            return "Login sekmingas!";
        }
        return "Neteisingi duomenys!";
    }
}
