package com.handson.basic.controller;




import com.handson.basic.service.CustomUserDetailsService;
import com.handson.basic.util.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private AuthenticationManager authManager;


    @Autowired
    private JWTUtil jwtUtil;


    @Autowired
    private CustomUserDetailsService userDetailsService;


    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        return jwtUtil.generateToken(userDetails.getUsername());
    }


    public static class AuthRequest {
        private String username;
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


