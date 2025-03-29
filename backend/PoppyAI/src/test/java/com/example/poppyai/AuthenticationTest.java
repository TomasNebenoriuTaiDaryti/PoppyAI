package com.example.poppyai;

import com.example.poppyai.model.User;
import com.example.poppyai.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testSuccessfulRegistration() throws Exception {
        String userJson = "{\"username\":\"testuser\",\"password\":\"password123\",\"email\":\"test@example.com\"}";

        mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(userJson)).andExpect(status().isOk()).andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testDuplicateUsernameRegistration() throws Exception {
        User user = new User("test", "password", "existing@example.com");
        userRepo.save(user);

        String userJson = "{\"username\":\"test\",\"password\":\"password123\",\"email\":\"new@example.com\"}";

        mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(userJson)).andExpect(status().isBadRequest()).andExpect(content().string("Username taken"));
    }

    @Test
    public void testValidLogin() throws Exception {
        String encodedPassword = passwordEncoder.encode("password123");
        User user = new User("userIsValid", encodedPassword, "valid@example.com");
        userRepo.save(user);

        String loginJson = "{\"username\":\"userIsValid\",\"password\":\"password123\"}";

        mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(loginJson)).andExpect(status().isOk()).andExpect(content().string("userIsValid"));
    }

    @Test
    public void testInvalidLogin() throws Exception {
        String loginJson = "{\"username\":\"userIsInvalid\",\"password\":\"wrongpassword\"}";

        mockMvc.perform(post("/api/login").contentType(MediaType.APPLICATION_JSON).content(loginJson)).andExpect(status().isUnauthorized()).andExpect(content().string("Invalid credentials"));
    }


    @Test
    @Transactional
    public void testPasswordHashing() {
        String rawPassword = "secret123";
        User user = new User("user", rawPassword, "user@example.com");
        userRepo.saveAndFlush(user);

        User savedUser = userRepo.findByUsername("user");

        assertNotEquals(passwordEncoder.matches(rawPassword, savedUser.getPassword()), "Passwordas nebus lygus uzkoduotam passwordui");
    }
}