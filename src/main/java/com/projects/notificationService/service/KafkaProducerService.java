package com.projects.notificationService.service;

import com.projects.notificationService.constants.KafkaTopics;
import com.projects.notificationService.dto.MessageResponse;
import com.projects.notificationService.dto.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public MessageResponse publish(NotificationEvent event) {

        /*
        Here we use message key as a eventId
        Kafka routes records with the same key to the same partition.
        This preserves ordering for related events and provides deterministic partitioning.
        */
        kafkaTemplate.send(
                KafkaTopics.NOTIFICATION_EVENTS,
                event.getEventId(),
                event
        );

        return new MessageResponse("Event published successfully");
    }
}
