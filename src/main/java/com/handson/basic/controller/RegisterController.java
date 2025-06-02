package com.handson.basic.controller;


import com.handson.basic.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class RegisterController {


    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public String register(@RequestBody @Valid RegisterRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        userDetailsService.save(request.getUsername(), encodedPassword);
        return "User registered successfully";
    }


    public static class RegisterRequest {
        @NotBlank
        @Size(min = 3, max = 20)
        private String username;


        @NotBlank
        @Size(min = 8)
        private String password;
        // Getters and setters
        public String getUsername() {
            return username;
        }


        public void setUsername(String username) {
            this.username = username;
        }


        public String getPassword() {
            return password;
        }


        public void setPassword(String password) {
            this.password = password;
        }
    }
}
