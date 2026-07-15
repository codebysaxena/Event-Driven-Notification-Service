package com.projects.notificationService.service;

import com.projects.notificationService.entity.NotificationDelivery;

public interface NotificationDeliveryService {
    void processEmail(NotificationDelivery emailDelivery);
    void processSms(NotificationDelivery smsDelivery);
    void processPush(NotificationDelivery pushDelivery);
}
