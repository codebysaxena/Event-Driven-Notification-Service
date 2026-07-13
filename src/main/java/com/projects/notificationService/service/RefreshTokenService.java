package com.projects.notificationService.service;

import com.projects.notificationService.dto.RefreshTokenResponse;
import com.projects.notificationService.entity.RefreshToken;
import com.projects.notificationService.exception.InvalidTokenException;
import com.projects.notificationService.exception.TokenNotFoundException;
import com.projects.notificationService.repository.RefreshTokenRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final CustomUserDetailsService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(CustomUserDetailsService userService, JwtService jwtService,
                               RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateRefreshToken(UserDetails userDetails){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(userDetails.getUsername());

        String uuidStr = UUID.randomUUID().toString();
        refreshToken.setToken(uuidStr);

        Date currentDate = new Date();
        // Calculate 7 days in milliseconds (7 * 24 * 60 * 60 * 1000)
        long sevenDaysInMs = 7L * 24 * 60 * 60 * 1000;
        //Create the future date
        Date expireDate = new Date(currentDate.getTime() + sevenDaysInMs);
        refreshToken.setExpiryTime(expireDate);

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public boolean validateToken(RefreshToken token, UserDetails userDetails){
        String userEmail = userDetails.getUsername();
        return token.getUsername().equals(userEmail) && !isTokenExpire(token);
    }

    public boolean isTokenExpire(RefreshToken token){
        return token.getExpiryTime().before(new Date());
    }

    public void deleteToken(String Token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(Token).orElse(null);
        if(refreshToken != null) refreshTokenRepository.delete(refreshToken);
    }

    public RefreshTokenResponse refreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElse(null);
        if(refreshToken == null){
            throw new TokenNotFoundException("Refresh Token not found");
        }

        String userEmail = refreshToken.getUsername();
        UserDetails userDetails = userService.loadUserByUsername(userEmail);

        if(!validateToken(refreshToken, userDetails)){
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token is invalid");
        }

        refreshTokenRepository.delete(refreshToken);

        String access_token = jwtService.generateToken(userDetails);
        String refresh_token = generateRefreshToken(userDetails);

        return new RefreshTokenResponse(access_token, refresh_token);
    }
}
