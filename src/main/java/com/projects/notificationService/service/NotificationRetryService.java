package com.projects.notificationService.service;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;
import com.projects.notificationService.constants.NotificationRetryConstants;
import com.projects.notificationService.entity.NotificationDelivery;
import com.projects.notificationService.repository.NotificationDeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationRetryService {
    private final NotificationDeliveryRepository deliveryRepository;
    private final NotificationDeliveryService deliveryService;

    private static final Logger log = LoggerFactory.getLogger(NotificationRetryService.class);

    @Autowired
    public NotificationRetryService(NotificationDeliveryRepository deliveryRepository,
                                    NotificationDeliveryService deliveryService){
        this.deliveryRepository = deliveryRepository;
        this.deliveryService = deliveryService;
    }

    @Scheduled(fixedDelay = 60000)
    public void retryNotificationDelivery(){
        List<NotificationDelivery> deliveryList = deliveryRepository.
                findTop100ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(DeliveryStatus.FAILED, NotificationRetryConstants.MAX_RETRY_COUNT);

        log.info("Retrying {} failed notifications", deliveryList.size());

        for(NotificationDelivery delivery: deliveryList){
            log.info("Retrying notification delivery id={}", delivery.getId());

            delivery.setStatus(DeliveryStatus.PROCESSING);
            deliveryRepository.save(delivery);

            delivery.setRetryCount(delivery.getRetryCount() + 1);
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
                if(delivery.getRetryCount() >= NotificationRetryConstants.MAX_RETRY_COUNT){
                    delivery.setStatus(DeliveryStatus.DEAD);
                }
                else{
                    delivery.setStatus(DeliveryStatus.FAILED);
                }
                delivery.setReason(e.getMessage());
            }
            deliveryRepository.save(delivery);
        }
    }
}
