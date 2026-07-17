package com.projects.notificationService.service;

import com.projects.notificationService.constants.KafkaTopics;
import com.projects.notificationService.dto.DeliveryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DeliveryEventProducerService {
    private final KafkaTemplate<String, DeliveryEvent> kafkaTemplate;

    @Autowired
    public DeliveryEventProducerService(KafkaTemplate<String, DeliveryEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDeliveryEvent(DeliveryEvent event){
        try {
            // Block and wait for Kafka broker acknowledgement (timeout after 5 seconds)
            kafkaTemplate.send(KafkaTopics.DELIVERY_EVENTS, event).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish outbox event to Kafka", e);
        }
    }
}
