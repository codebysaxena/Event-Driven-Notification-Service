package com.projects.notificationService.service;

import com.projects.notificationService.constants.KafkaTopics;
import com.projects.notificationService.dto.NotificationDeliveryDlq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationDeliveryDlqConsumerService {
    private static final Logger log = LoggerFactory.getLogger(NotificationDeliveryDlqConsumerService.class);

    private final NotificationDeliveryDlqService notificationDeliveryDlqService;

    @Autowired
    public NotificationDeliveryDlqConsumerService(NotificationDeliveryDlqService notificationDeliveryDlqService){
        this.notificationDeliveryDlqService = notificationDeliveryDlqService;
    }

    @KafkaListener(topics = KafkaTopics.DELIVERY_DLQ, groupId = "delivery-group-dlq")
    public void consumeDeadNotificationDelivery(NotificationDeliveryDlq event){
        log.info(
                "DLQ Notification Received. deliveryId={}, channel={}, reason={}",
                event.getDeliveryId(),
                event.getChannel(),
                event.getReason()
        );
        try{
           notificationDeliveryDlqService.processDeadNotificationDelivery(event);
        } catch (Exception e) {
            log.error("Error while consuming dead notificationDelivery error:{}", e.getMessage(), e);
            throw e;
        }
    }
}
