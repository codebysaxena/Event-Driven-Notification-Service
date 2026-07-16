package com.projects.notificationService.service;

import com.projects.notificationService.constants.RedisKeys;
import com.projects.notificationService.dto.*;
import com.projects.notificationService.entity.NotificationPreference;
import com.projects.notificationService.constants.Roles;
import com.projects.notificationService.entity.User;
import com.projects.notificationService.exception.InvalidTokenException;
import com.projects.notificationService.exception.UserAlreadyExistsException;
import com.projects.notificationService.repository.NotificationPreferenceRepository;
import com.projects.notificationService.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RedisService redisService;

    @Autowired
    public AuthServiceImpl(
            UserRepository userRepository,
            NotificationPreferenceRepository preferenceRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            RedisService redisService) {

        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.redisService = redisService;
    }

    @Override
    public MessageResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistsException("Email already exists");
        }
        if(userRepository.existsByUsername(request.getUsername())){
            throw new UserAlreadyExistsException("Username already exists");
        }

        //encode user password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(encodedPassword);
        newUser.setPhone(request.getPhone());
        newUser.setRole(Roles.USER);

        User savedUser = userRepository.save(newUser);

        NotificationPreference preference = new NotificationPreference();
        preference.setEmailEnabled(true);
        preference.setPushEnabled(true);
        preference.setSmsEnabled(true);

        //Associate User, This is the key line that establishes the OneToOne relationship.
        preference.setUser(savedUser);

        preferenceRepository.save(preference);

        return new MessageResponse("User registered successfully");
    }

    @Override
    public RefreshTokenResponse login(LoginRequest request) {
        // Programmatically verify the credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String access_token = jwtService.generateToken(userDetails);
        String refresh_token = refreshTokenService.generateRefreshToken(userDetails);

        return new RefreshTokenResponse(access_token, refresh_token);
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request){
        String refreshToken = request.getRefreshToken();
        return refreshTokenService.refreshToken(
                refreshToken
        );
    }

    @Override
    public MessageResponse logout(LogoutRequest req, HttpServletRequest request){
        String refreshToken = req.getRefreshToken();
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new InvalidTokenException("Access token missing");
        }

        String accessToken = authHeader.substring(7);

        try{
            Date currDate = new Date();
            Date expirationDate = jwtService.tokenExpirationTime(accessToken);
            long secondsRemaining = ChronoUnit.SECONDS.between(currDate.toInstant(), expirationDate.toInstant());

            if(secondsRemaining > 0){
                String key = RedisKeys.BLACKLIST + accessToken;

                //blacklist the accessToken
                redisService.set(
                        key,
                        "BLACKLISTED",
                        secondsRemaining
                );
            }
        }
        catch (JwtException | IllegalArgumentException e){
            throw new InvalidTokenException(
                    "Invalid access token"
            );
        }
        refreshTokenService.deleteToken(refreshToken);

        return new MessageResponse("User Logged out successfully");
    }
}
