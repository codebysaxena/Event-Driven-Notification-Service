package com.projects.notificationService.service;

import com.projects.notificationService.entity.NotificationDelivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationDeliveryServiceImpl implements NotificationDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(
            NotificationDeliveryServiceImpl.class
    );

    @Override
    public void processEmail(NotificationDelivery emailDelivery) {
        try {
            // send
            log.info("Email notification sent successfully");
        }
        catch(Exception e){
            log.error("Email service having some issue");
            throw e;
        }
    }

    @Override
    public void processSms(NotificationDelivery smsDelivery) {
        try {
            // send
            log.info("SMS notification sent successfully");
        }
        catch(Exception e){
            log.error("SMS service having some issue");
            throw e;
        }
    }

    @Override
    public void processPush(NotificationDelivery pushDelivery) {
        try {
            // send
            log.info("Push notification sent successfully");
        }
        catch(Exception e){
            log.error("Push service having some issue");
            throw e;
        }
    }
}
