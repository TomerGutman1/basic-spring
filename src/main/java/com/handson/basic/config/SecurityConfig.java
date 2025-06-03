package com.handson.basic.config;


import com.handson.basic.service.CustomUserDetailsService;
import com.handson.basic.util.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    private JWTFilter jwtFilter;


    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        // Disable CSRF protection (since we're using JWT, not cookies)
        http.csrf(csrf -> csrf.disable());


        // Make session stateless (no server-side session will be created)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );


        // Define which endpoints are public and which require authentication
        http.authorizeHttpRequests(auth ->
                auth
                        .requestMatchers("/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**").permitAll()  // Public endpoints
                        .anyRequest().authenticated()             // Everything else is protected
        );


        // Register our custom JWT filter before the default username/password filter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        // Return the configured SecurityFilterChain
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
