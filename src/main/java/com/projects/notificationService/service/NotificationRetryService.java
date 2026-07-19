package com.projects.notificationService.service;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;
import com.projects.notificationService.constants.NotificationRetryConstants;
import com.projects.notificationService.dto.NotificationDeliveryDlq;
import com.projects.notificationService.entity.NotificationDelivery;
import com.projects.notificationService.repository.NotificationDeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationRetryService {
    private final NotificationDeliveryRepository deliveryRepository;
    private final NotificationDeliveryService deliveryService;
    private final NotificationDeliveryDlqProducerService dlqProducerService;

    private static final Logger log = LoggerFactory.getLogger(NotificationRetryService.class);

    @Autowired
    public NotificationRetryService(NotificationDeliveryRepository deliveryRepository,
                                    NotificationDeliveryService deliveryService,
                                    NotificationDeliveryDlqProducerService dlqProducerService){
        this.deliveryRepository = deliveryRepository;
        this.deliveryService = deliveryService;
        this.dlqProducerService = dlqProducerService;
    }

    private NotificationDeliveryDlq getDeadDeliveryEvent(NotificationDelivery delivery){
        NotificationDeliveryDlq deadEvent = new NotificationDeliveryDlq();
        deadEvent.setDeliveryId(delivery.getId());
        deadEvent.setChannel(delivery.getChannel());
        deadEvent.setReason(delivery.getReason());
        deadEvent.setRetryCount(delivery.getRetryCount());
        deadEvent.setDeadAt(LocalDateTime.now());
        return deadEvent;
    }

    @Scheduled(fixedDelay = 60000)
    public void retryNotificationDelivery(){
        List<NotificationDelivery> deliveryList = deliveryRepository.
                findTop100ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(DeliveryStatus.FAILED, NotificationRetryConstants.MAX_RETRY_COUNT);

        log.info("Retrying {} failed notifications", deliveryList.size());

        for(NotificationDelivery delivery: deliveryList){
            log.info("Retrying notification delivery id={}", delivery.getId());

            delivery.setRetryCount(delivery.getRetryCount() + 1);
            delivery.setStatus(DeliveryStatus.PROCESSING);
            deliveryRepository.save(delivery);

            try{
                if(delivery.getChannel() == NotificationChannel.EMAIL)
                    deliveryService.processEmail(delivery);

                else if(delivery.getChannel() == NotificationChannel.SMS)
                    deliveryService.processSms(delivery);

                else if(delivery.getChannel() == NotificationChannel.PUSH)
                    deliveryService.processPush(delivery);

                delivery.setStatus(DeliveryStatus.SENT);
                delivery.setReason(null);
            }
            catch (Exception e){
                log.error("Retry attempt failed for deliveryId={}, channel={}. Attempt {} of {}. Error: {}",
                        delivery.getId(), delivery.getChannel(), delivery.getRetryCount(),
                        NotificationRetryConstants.MAX_RETRY_COUNT, e.getMessage(), e);

                if(delivery.getRetryCount() >= NotificationRetryConstants.MAX_RETRY_COUNT){
                    delivery.setStatus(DeliveryStatus.DEAD);
                }
                else{
                    delivery.setStatus(DeliveryStatus.FAILED);
                }
                delivery.setReason(e.getMessage());

                if(delivery.getStatus() == DeliveryStatus.DEAD){
                    NotificationDeliveryDlq deadEvent = getDeadDeliveryEvent(delivery);
                    try {
                        dlqProducerService.publishDeadNotificationDelivery(deadEvent);
                    }
                    catch(Exception ex) {
                        log.error("Failed to publish DLQ event", ex);
                    }
                }
            }
            deliveryRepository.save(delivery);
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void recoverStuckNotificationDelivery(){
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(5);
        List<NotificationDelivery> deliveryList = deliveryRepository.
                findByStatusAndUpdatedAtBefore(DeliveryStatus.PROCESSING, timeout);

        log.info("Recovering {} stuck processing notifications", deliveryList.size());

        for(NotificationDelivery delivery: deliveryList){
            if(delivery.getRetryCount() >= NotificationRetryConstants.MAX_RETRY_COUNT){
                delivery.setStatus(DeliveryStatus.DEAD);
                delivery.setReason("Exceeded max retries while stuck in PROCESSING");

                NotificationDeliveryDlq deadEvent = getDeadDeliveryEvent(delivery);
                try {
                    dlqProducerService.publishDeadNotificationDelivery(deadEvent);
                }
                catch(Exception ex) {
                    log.error("Failed to publish DLQ event", ex);
                }
            }
            else{
                delivery.setStatus(DeliveryStatus.FAILED);
                delivery.setReason("Recovered from stuck PROCESSING state");
            }
            deliveryRepository.save(delivery);

            log.info("Recovered stuck notification delivery id={}", delivery.getId());
        }
    }
}
