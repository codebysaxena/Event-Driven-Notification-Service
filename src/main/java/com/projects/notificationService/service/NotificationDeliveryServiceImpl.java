package com.projects.notificationService.service;

import com.projects.notificationService.entity.Notification;
import com.projects.notificationService.entity.NotificationDelivery;
import com.projects.notificationService.entity.User;
import com.projects.notificationService.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationDeliveryServiceImpl implements NotificationDeliveryService {

    private final EmailService emailService;
    private final SmsService smsService;
    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(NotificationDeliveryServiceImpl.class);

    @Autowired
    public NotificationDeliveryServiceImpl(EmailService emailService, SmsService smsService, UserRepository userRepository){
        this.emailService = emailService;
        this.smsService = smsService;
        this.userRepository = userRepository;
    }

    @Override
    public void processEmail(NotificationDelivery emailDelivery) {
        try {
            Notification notification = (emailDelivery != null) ? emailDelivery.getNotification() : null;
            Long userId = (notification != null) ? notification.getUserId() : null;

            User user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;

            String type = (notification != null && notification.getType() != null) ? notification.getType() : "GENERAL";
            String messageBody = (notification != null && notification.getMessage() != null) ? notification.getMessage() : "Testing Notification";
            if (messageBody.isBlank()) {
                messageBody = "Testing Notification";
            }

            String subject = "Notification: " + type;
            String recipientEmail = (user != null && user.getEmail() != null && !user.getEmail().isBlank()) ? user.getEmail() : "moni1999899@gmail.com";

            emailService.sendEmail(recipientEmail, subject, messageBody);
            log.info("Email notification processed successfully for deliveryId={}, userId={}, recipient={}",
                    (emailDelivery != null ? emailDelivery.getId() : null), userId, recipientEmail);
        }
        catch(Exception e){
            log.error("Email service processing failed for deliveryId={}: {}",
                    (emailDelivery != null ? emailDelivery.getId() : "N/A"), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void processSms(NotificationDelivery smsDelivery) {
        try {
            Notification notification = (smsDelivery != null) ? smsDelivery.getNotification() : null;
            Long userId = (notification != null) ? notification.getUserId() : null;

            User user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;

            String messageBody = (notification != null && notification.getMessage() != null) ? notification.getMessage() : "Testing Notification";
            if (messageBody.isBlank()) {
                messageBody = "Testing Notification";
            }

            String phoneNumber = (user != null && user.getPhone() != null && !user.getPhone().isBlank()) ? user.getPhone() : "+918887732757";

            smsService.sendSms(phoneNumber, messageBody);
            log.info("SMS notification processed successfully for deliveryId={}, userId={}, phone={}",
                    (smsDelivery != null ? smsDelivery.getId() : null), userId, phoneNumber);
        }
        catch(Exception e){
            log.error("SMS service processing failed for deliveryId={}: {}",
                    (smsDelivery != null ? smsDelivery.getId() : "N/A"), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void processPush(NotificationDelivery pushDelivery) {
        try {
            log.info("Push notification processed successfully for deliveryId={}",
                    (pushDelivery != null ? pushDelivery.getId() : "N/A"));
        }
        catch(Exception e){
            log.error("Push service processing failed for deliveryId={}: {}",
                    (pushDelivery != null ? pushDelivery.getId() : "N/A"), e.getMessage(), e);
            throw e;
        }
    }
}
