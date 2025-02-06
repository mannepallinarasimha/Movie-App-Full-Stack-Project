package com.moviemix.MovieApi.controllers;

import com.moviemix.MovieApi.auth.entities.RefreshToken;
import com.moviemix.MovieApi.auth.entities.User;
import com.moviemix.MovieApi.auth.service.AuthService;
import com.moviemix.MovieApi.auth.service.JWTService;
import com.moviemix.MovieApi.auth.service.RefreshTokenService;
import com.moviemix.MovieApi.auth.utils.AuthResponse;
import com.moviemix.MovieApi.auth.utils.LoginRequest;
import com.moviemix.MovieApi.auth.utils.RefreshTokenRequest;
import com.moviemix.MovieApi.auth.utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="api/v1/auth/")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JWTService jwtService;
    //register
    @PostMapping(path="register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }
    //login
    @PostMapping(path="login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    //generate valid jwt and refresh token
    @PostMapping(path="refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();
        String accessToken = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthResponse.builder()

                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());

    }
}
