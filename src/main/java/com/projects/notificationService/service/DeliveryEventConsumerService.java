package com.projects.notificationService.service;

import com.projects.notificationService.constants.KafkaTopics;
import com.projects.notificationService.dto.DeliveryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DeliveryEventConsumerService {
    private static final Logger log = LoggerFactory.getLogger(DeliveryEventConsumerService.class);

    private final DeliveryEventProcessingService deliveryEventProcessingService;

    @Autowired
    public DeliveryEventConsumerService(DeliveryEventProcessingService deliveryEventProcessingService){
        this.deliveryEventProcessingService = deliveryEventProcessingService;
    }

    @KafkaListener(topics = KafkaTopics.DELIVERY_EVENTS, groupId = "delivery-group")
    void consumeDeliveryEvent(DeliveryEvent event){
        log.info("Received deliveryEvent: id={}", event.getDeliveryId());

        try{
            deliveryEventProcessingService.processDeliveryEvent(event);
        } catch (Exception e) {
            log.error("Error while consuming deliveryEvent error:{}", e.getMessage(), e);
            throw e;
        }
    }
}
