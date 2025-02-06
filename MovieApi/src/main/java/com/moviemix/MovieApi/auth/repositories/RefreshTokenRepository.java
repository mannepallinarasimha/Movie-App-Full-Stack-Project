package com.moviemix.MovieApi.auth.repositories;

import com.moviemix.MovieApi.auth.entities.RefreshToken;
import com.moviemix.MovieApi.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
