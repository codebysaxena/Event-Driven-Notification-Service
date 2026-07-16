package com.projects.notificationService.service;
 
import com.projects.notificationService.dto.NotificationEvent;
import com.projects.notificationService.constants.KafkaTopics;
import com.projects.notificationService.exception.EventAlreadyExistsException;
import com.projects.notificationService.exception.EventRateLimitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
 
@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final NotificationProcessingService notificationProcessingService;

    @Autowired
    public KafkaConsumerService(NotificationProcessingService notificationProcessingService) {
        this.notificationProcessingService = notificationProcessingService;
    }

    //Since we already configured group-id in application.properties, no need to add here again
    @KafkaListener(topics = KafkaTopics.NOTIFICATION_EVENTS)
    public void consume(NotificationEvent event){
        log.info(
                "Received Event: eventId={}, userId={}, type={}, message={}",
                event.getEventId(),
                event.getUserId(),
                event.getType(),
                event.getMessage()
        );
        try {
            notificationProcessingService.processEvent(event);
        }
        catch(EventAlreadyExistsException | EventRateLimitException e){
            log.warn("Business exception: {}", e.getMessage());
        }
        catch (Exception e) {
            log.error("Error processing consumed event: {}", e.getMessage(), e);
            throw e;
        }
    }
}
