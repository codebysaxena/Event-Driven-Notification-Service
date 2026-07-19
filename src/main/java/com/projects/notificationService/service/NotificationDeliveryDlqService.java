package com.projects.notificationService.service;

import com.projects.notificationService.dto.NotificationDeliveryDlq;

public interface NotificationDeliveryDlqService {
    void processDeadNotificationDelivery(NotificationDeliveryDlq event);
}
