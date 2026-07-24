package com.projects.notificationService.service;

import com.projects.notificationService.entity.NotificationDelivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationDeliveryServiceImpl implements NotificationDeliveryService {

    private final EmailService emailService;
    private final SmsService smsService;

    private static final Logger log = LoggerFactory.getLogger(NotificationDeliveryServiceImpl.class);

    @Autowired
    public NotificationDeliveryServiceImpl(EmailService emailService, SmsService smsService){
        this.emailService = emailService;
        this.smsService = smsService;
    }

    @Override
    public void processEmail(NotificationDelivery emailDelivery) {
        try {
            Long userId = (emailDelivery.getNotification() != null) ? emailDelivery.getNotification().getUserId() : null;
            String type = (emailDelivery.getNotification() != null) ? emailDelivery.getNotification().getType() : "GENERAL";
            String messageBody = (emailDelivery.getNotification() != null) ? emailDelivery.getNotification().getMessage() : "Testing Notification";

            String subject = "Notification: " + type;
            String recipientEmail = "moni1999899@gmail.com";

            emailService.sendEmail(recipientEmail, subject, messageBody);
            log.info("Email notification processed successfully for deliveryId={}, userId={}", emailDelivery.getId(), userId);
        }
        catch(Exception e){
            log.error("Email service processing failed for deliveryId={}", emailDelivery.getId(), e);
            throw e;
        }
    }

    @Override
    public void processSms(NotificationDelivery smsDelivery) {
        try {
            Long userId = (smsDelivery.getNotification() != null) ? smsDelivery.getNotification().getUserId() : null;
            String messageBody = (smsDelivery.getNotification() != null) ? smsDelivery.getNotification().getMessage() : "Testing Notification";

            String phoneNumber = "+918887732757";

            smsService.sendSms(phoneNumber, messageBody);
            log.info("SMS notification processed successfully for deliveryId={}, userId={}", smsDelivery.getId(), userId);
        }
        catch(Exception e){
            log.error("SMS service processing failed for deliveryId={}", smsDelivery.getId(), e);
            throw e;
        }
    }

    @Override
    public void processPush(NotificationDelivery pushDelivery) {
        try {
            log.info("Push notification processed successfully for deliveryId={}", pushDelivery.getId());
        }
        catch(Exception e){
            log.error("Push service processing failed for deliveryId={}", pushDelivery.getId(), e);
            throw e;
        }
    }
}
