package com.projects.notificationService.service;

import com.projects.notificationService.dto.DeliveryEvent;

public interface DeliveryEventProcessingService {
    public void processDeliveryEvent(DeliveryEvent event);
}
