package com.projects.notificationService.service;

import com.projects.notificationService.dto.NotificationEvent;

public interface NotificationProcessingService {
    void processEvent(NotificationEvent event);
}
