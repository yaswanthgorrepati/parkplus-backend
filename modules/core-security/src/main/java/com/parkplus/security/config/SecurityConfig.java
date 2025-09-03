package com.parkplus.security.config;

import com.parkplus.security.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtFilter;


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/api/v1/auth/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                                // bookings
                                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/bookings/preview").permitAll()
                                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/bookings").hasAnyRole("CUSTOMER", "HOST", "ADMIN")
                                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/bookings/*/decide").hasAnyRole("HOST", "ADMIN")
                                // listings
                                .requestMatchers(HttpMethod.GET, "/api/v1/listings/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/listings").hasAnyRole("HOST", "ADMIN")
                                //availability
                                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/listings/*/availability/**").hasAnyRole("HOST", "ADMIN")
                                //razor pay payment
                                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/payments/create-order").hasAnyRole("CUSTOMER", "HOST", "ADMIN")
                                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/payments/verify").hasAnyRole("CUSTOMER", "HOST", "ADMIN")
                                //mastrData
                                .requestMatchers(HttpMethod.GET, "/api/v1/master/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/master/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173",
                "https://parkplus-topaz.vercel.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}