package com.projects.notificationService.service;

import com.projects.notificationService.constants.NotificationRetryConstants;
import com.projects.notificationService.constants.OutboxConstants;
import com.projects.notificationService.constants.OutboxEventStatus;
import com.projects.notificationService.dto.DeliveryEvent;
import com.projects.notificationService.entity.OutboxEvent;
import com.projects.notificationService.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboxPublisherSchedulerService {
    private final OutboxEventRepository outboxEventRepository;
    private DeliveryEventProducerService deliveryEventProducerService;

    private static final Logger log = LoggerFactory.
            getLogger(OutboxPublisherSchedulerService.class);

    @Autowired
    public OutboxPublisherSchedulerService(OutboxEventRepository outboxEventRepository,
                                           DeliveryEventProducerService deliveryEventProducerService){
        this.outboxEventRepository = outboxEventRepository;
        this.deliveryEventProducerService = deliveryEventProducerService;
    }

    private DeliveryEvent createDeliveryEvent(String payload){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);
            DeliveryEvent event = new DeliveryEvent();

            event.setDeliveryId(jsonNode.get("deliveryId").asLong());
            return event;
        }
        catch(Exception e){
            throw new RuntimeException("Invalid payload", e);
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void publishPendingOutboxEvents(){
        List<OutboxEventStatus> targetStatuses = List.of(OutboxEventStatus.PENDING, OutboxEventStatus.FAILED);
        List<OutboxEvent> outboxEvents = outboxEventRepository.
                findTop100ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc
                        (targetStatuses, OutboxConstants.MAX_RETRY_COUNT);

        log.info("Publishing {} outbox events", outboxEvents.size());

        for(OutboxEvent outboxEvent: outboxEvents){
            outboxEvent.setRetryCount(outboxEvent.getRetryCount() + 1);
            outboxEvent.setStatus(OutboxEventStatus.PROCESSING);
            outboxEventRepository.save(outboxEvent);
            log.info("publish pending deliveryEvent, id={}", outboxEvent.getId());

            try{
                String payload = outboxEvent.getPayload();
                DeliveryEvent event = createDeliveryEvent(payload);
                deliveryEventProducerService.publishDeliveryEvent(event);

                log.info("Outbox event published successfully. id={}", outboxEvent.getId());
                outboxEvent.setStatus(OutboxEventStatus.SENT);
            }
            catch (Exception e){
                log.error("Failed to publish outbox event id={}. Attempt {} of {}. Error: {}",
                        outboxEvent.getId(), outboxEvent.getRetryCount(),
                        OutboxConstants.MAX_RETRY_COUNT, e.getMessage(), e);

                if(outboxEvent.getRetryCount() >= OutboxConstants.MAX_RETRY_COUNT){
                    outboxEvent.setStatus(OutboxEventStatus.DEAD);
                }
                else outboxEvent.setStatus(OutboxEventStatus.FAILED);
            }
            outboxEventRepository.save(outboxEvent);
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void recoverStuckOutboxEvents(){
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(5);
        List<OutboxEvent> outboxEventList = outboxEventRepository.
                findByStatusAndUpdatedAtBefore(OutboxEventStatus.PROCESSING, timeout);

        log.info("Recovering {} stuck processing OutboxEvents", outboxEventList.size());

        for(OutboxEvent event: outboxEventList){
            if(event.getRetryCount() >= NotificationRetryConstants.MAX_RETRY_COUNT){
                event.setStatus(OutboxEventStatus.DEAD);
            }
            else{
                event.setStatus(OutboxEventStatus.FAILED);
            }
            outboxEventRepository.save(event);

            log.info(
                    "Recovering stuck OutboxEvent id={}, retryCount={}",
                    event.getId(),
                    event.getRetryCount()
            );
        }
    }
}
