package com.projects.notificationService.service;

import com.projects.notificationService.dto.MessageResponse;
import com.projects.notificationService.dto.PreferenceRequest;
import com.projects.notificationService.dto.PreferenceResponse;
import com.projects.notificationService.entity.NotificationPreference;
import com.projects.notificationService.exception.NotificationPreferenceNotFoundException;
import com.projects.notificationService.repository.NotificationPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService{
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    public NotificationPreferenceServiceImpl(NotificationPreferenceRepository notificationPreferenceRepository){
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    @Override
    public PreferenceResponse getPreferences(Long userId) {
        NotificationPreference notificationPreference = notificationPreferenceRepository.findPreferencesByUserId(userId).orElse(null);
        if(notificationPreference == null){
            throw new NotificationPreferenceNotFoundException("Notification Preferences not found");
        }

        PreferenceResponse preferenceResponse = new PreferenceResponse();

        preferenceResponse.setEmailEnabled(notificationPreference.isEmailEnabled());
        preferenceResponse.setPushEnabled(notificationPreference.isPushEnabled());
        preferenceResponse.setSmsEnabled(notificationPreference.isSmsEnabled());
        return preferenceResponse;
    }

    @Override
    public MessageResponse updatePreferences(Long userId, PreferenceRequest request) {
        NotificationPreference notificationPreference = notificationPreferenceRepository.findPreferencesByUserId(userId).orElse(null);
        if(notificationPreference == null){
            throw new NotificationPreferenceNotFoundException("Notification Preferences not found");
        }

        notificationPreference.setSmsEnabled(request.isSmsEnabled());
        notificationPreference.setPushEnabled(request.isPushEnabled());
        notificationPreference.setEmailEnabled(request.isEmailEnabled());

        notificationPreferenceRepository.save(notificationPreference);

        return new MessageResponse("Notification preferences updated successfully");
    }

    @Override
    public PreferenceResponse getPreferencesByEmail(String email) {
        NotificationPreference notificationPreference = notificationPreferenceRepository.findByUserEmail(email).orElse(null);
        if(notificationPreference == null){
            throw new NotificationPreferenceNotFoundException("Notification Preferences not found");
        }

        PreferenceResponse preferenceResponse = new PreferenceResponse();
        preferenceResponse.setEmailEnabled(notificationPreference.isEmailEnabled());
        preferenceResponse.setPushEnabled(notificationPreference.isPushEnabled());
        preferenceResponse.setSmsEnabled(notificationPreference.isSmsEnabled());
        return preferenceResponse;
    }

    @Override
    public MessageResponse updatePreferencesByEmail(String email, PreferenceRequest request) {
        NotificationPreference notificationPreference = notificationPreferenceRepository.findByUserEmail(email).orElse(null);
        if(notificationPreference == null){
            throw new NotificationPreferenceNotFoundException("Notification Preferences not found");
        }

        notificationPreference.setSmsEnabled(request.isSmsEnabled());
        notificationPreference.setPushEnabled(request.isPushEnabled());
        notificationPreference.setEmailEnabled(request.isEmailEnabled());

        notificationPreferenceRepository.save(notificationPreference);

        return new MessageResponse("Notification preferences updated successfully");
    }
}
