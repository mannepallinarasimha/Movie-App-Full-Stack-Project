package com.moviemix.MovieApi.auth.service;

import com.moviemix.MovieApi.auth.entities.RefreshToken;
import com.moviemix.MovieApi.auth.entities.User;
import com.moviemix.MovieApi.auth.repositories.RefreshTokenRepository;
import com.moviemix.MovieApi.auth.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String username){
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User NOT found with email : " + username));
        RefreshToken refreshToken = user.getRefreshToken();
        if(refreshToken == null){
            long refreshTokenValidity = 30*10000;
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken){
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new RuntimeException("Refresh token NOT found"));
       if(refToken.getExpirationTime().compareTo(Instant.now())<0){
           refreshTokenRepository.delete(refToken);
           throw  new RuntimeException("Refresh token expired...");
       }
        return refToken;
    }
}
