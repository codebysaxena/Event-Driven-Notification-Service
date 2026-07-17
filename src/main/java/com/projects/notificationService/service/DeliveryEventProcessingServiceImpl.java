package com.projects.notificationService.service;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.dto.DeliveryEvent;
import com.projects.notificationService.entity.NotificationDelivery;
import com.projects.notificationService.repository.NotificationDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.projects.notificationService.constants.NotificationChannel.*;

@Service
public class DeliveryEventProcessingServiceImpl implements DeliveryEventProcessingService{
    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final NotificationDeliveryService notificationDeliveryService;

    @Autowired
    public DeliveryEventProcessingServiceImpl(NotificationDeliveryRepository notificationDeliveryRepository,
                                              NotificationDeliveryService notificationDeliveryService){
        this.notificationDeliveryRepository = notificationDeliveryRepository;
        this.notificationDeliveryService = notificationDeliveryService;
    }

    private void helper(NotificationDelivery delivery){
        if(delivery.getStatus() != DeliveryStatus.PENDING){
            return;
        }
        delivery.setStatus(DeliveryStatus.PROCESSING);
        delivery.setReason(null);
        notificationDeliveryRepository.save(delivery);

        try {
            if(delivery.getChannel() == EMAIL) notificationDeliveryService.processEmail(delivery);
            else if(delivery.getChannel() == PUSH) notificationDeliveryService.processPush(delivery);
            else if(delivery.getChannel() == SMS) notificationDeliveryService.processSms(delivery);

            delivery.setStatus(DeliveryStatus.SENT);
            delivery.setReason(null);
        }
        catch(Exception e){
            delivery.setStatus(DeliveryStatus.FAILED);
            delivery.setReason(e.getMessage());
        }
        notificationDeliveryRepository.save(delivery);
    }

    @Override
    public void processDeliveryEvent(DeliveryEvent event) {
        NotificationDelivery delivery = notificationDeliveryRepository.
                findById(event.getDeliveryId()).orElse(null);

        if(delivery == null) return;
        helper(delivery);
        System.out.println("All Notification sent as per User preferences");
    }
}
