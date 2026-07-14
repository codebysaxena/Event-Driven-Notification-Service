package com.projects.notificationService.controller;

import com.projects.notificationService.dto.MessageResponse;
import com.projects.notificationService.dto.NotificationEvent;
import com.projects.notificationService.service.KafkaProducerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EventController {
    private final KafkaProducerService kafkaProducerService;

    public EventController(KafkaProducerService kafkaProducerService){
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/events/send")
    public ResponseEntity<MessageResponse> publishEvent(@Valid @RequestBody NotificationEvent notificationEvent){
        MessageResponse res = kafkaProducerService.publish(notificationEvent);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(res);
    }
}
