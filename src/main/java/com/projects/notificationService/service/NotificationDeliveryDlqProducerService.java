package com.projects.notificationService.service;

import com.projects.notificationService.constants.KafkaTopics;
import com.projects.notificationService.dto.NotificationDeliveryDlq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class NotificationDeliveryDlqProducerService {
    private final KafkaTemplate<String, NotificationDeliveryDlq> kafkaTemplate;

    @Autowired
    public NotificationDeliveryDlqProducerService(KafkaTemplate<String, NotificationDeliveryDlq> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDeadNotificationDelivery(NotificationDeliveryDlq event){
        try {
            // Block and wait for Kafka broker acknowledgement (timeout after 5 seconds)
            kafkaTemplate.send(KafkaTopics.DELIVERY_DLQ, event).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish dead notification delivery to kafka", e);
        }
    }
}
