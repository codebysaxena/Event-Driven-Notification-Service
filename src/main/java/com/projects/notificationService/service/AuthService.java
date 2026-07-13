package com.projects.notificationService.service;

import com.projects.notificationService.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    MessageResponse register(RegisterRequest request);
    RefreshTokenResponse login(LoginRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    MessageResponse logout(LogoutRequest req, HttpServletRequest request);
}
