package com.projects.notificationService.service;

import com.projects.notificationService.dto.NotificationDeliveryDlq;
import com.projects.notificationService.entity.DeliveryDlq;
import com.projects.notificationService.repository.DeliveryDlqRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class NotificationDeliveryDlqServiceImpl implements NotificationDeliveryDlqService{
    private final DeliveryDlqRepository dlqRepository;

    public NotificationDeliveryDlqServiceImpl(DeliveryDlqRepository dlqRepository){
        this.dlqRepository = dlqRepository;
    }

    @Override
    @Transactional
    public void processDeadNotificationDelivery(NotificationDeliveryDlq event) {
        DeliveryDlq dlq = new DeliveryDlq();
        dlq.setDeliveryId(event.getDeliveryId());
        dlq.setChannel(event.getChannel());
        dlq.setReason(event.getReason());
        dlq.setRetryCount(event.getRetryCount());
        dlq.setDeadAt(event.getDeadAt());

        dlqRepository.save(dlq);
    }
}
