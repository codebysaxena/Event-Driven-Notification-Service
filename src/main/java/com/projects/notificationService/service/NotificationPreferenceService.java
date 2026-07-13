package com.projects.notificationService.service;

import com.projects.notificationService.dto.MessageResponse;
import com.projects.notificationService.dto.PreferenceRequest;
import com.projects.notificationService.dto.PreferenceResponse;

public interface NotificationPreferenceService {
    PreferenceResponse getPreferences(Long userId);
    MessageResponse updatePreferences(Long userId, PreferenceRequest request);
    PreferenceResponse getPreferencesByEmail(String email);
    MessageResponse updatePreferencesByEmail(String email, PreferenceRequest request);
}
