package com.projects.notificationService.service;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.entity.NotificationDelivery;
import com.projects.notificationService.repository.NotificationDeliveryRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationDeliveryServiceImpl implements NotificationDeliveryService {
    private final NotificationDeliveryRepository notificationDeliveryRepository;

    public NotificationDeliveryServiceImpl(NotificationDeliveryRepository notificationDeliveryRepository){
        this.notificationDeliveryRepository = notificationDeliveryRepository;
    }


    @Override
    public void processEmail(NotificationDelivery emailDelivery) {
        try {
            // send
            System.out.println("Email notification sent successfully");
            emailDelivery.setStatus(DeliveryStatus.SENT);
        }
        catch(Exception e){
            emailDelivery.setStatus(DeliveryStatus.FAILED);
            emailDelivery.setReason(e.getMessage());
        }

        notificationDeliveryRepository.save(emailDelivery);
    }

    @Override
    public void processSms(NotificationDelivery smsDelivery) {
        try {
            // send
            System.out.println("SMS notification sent successfully");
            smsDelivery.setStatus(DeliveryStatus.SENT);
        }
        catch(Exception e){
            smsDelivery.setStatus(DeliveryStatus.FAILED);
            smsDelivery.setReason(e.getMessage());
        }

        notificationDeliveryRepository.save(smsDelivery);
    }

    @Override
    public void processPush(NotificationDelivery pushDelivery) {
        try {
            // send
            System.out.println("Push notification sent successfully");
            pushDelivery.setStatus(DeliveryStatus.SENT);
        }
        catch(Exception e){
            pushDelivery.setStatus(DeliveryStatus.FAILED);
            pushDelivery.setReason(e.getMessage());
        }

        notificationDeliveryRepository.save(pushDelivery);
    }
}
