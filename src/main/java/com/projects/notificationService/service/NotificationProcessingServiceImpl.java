package com.projects.notificationService.service;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;
import com.projects.notificationService.constants.RedisKeys;
import com.projects.notificationService.dto.NotificationEvent;
import com.projects.notificationService.dto.PreferenceResponse;
import com.projects.notificationService.entity.Notification;
import com.projects.notificationService.entity.NotificationDelivery;
import com.projects.notificationService.exception.EventRateLimitException;
import com.projects.notificationService.repository.NotificationDeliveryRepository;
import com.projects.notificationService.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class NotificationProcessingServiceImpl implements NotificationProcessingService{
    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final NotificationPreferenceService NotificationPreferenceService;
    private final RedisService redisService;
    private final NotificationDeliveryService notificationDeliveryService;
    
    public NotificationProcessingServiceImpl(NotificationRepository notificationRepository,
                                             NotificationPreferenceService NotificationPreferenceService,
                                             RedisService redisService,
                                             NotificationDeliveryRepository notificationDeliveryRepository,
                                             NotificationDeliveryService notificationDeliveryService){
        this.notificationRepository = notificationRepository;
        this.NotificationPreferenceService = NotificationPreferenceService;
        this.redisService = redisService;
        this.notificationDeliveryRepository = notificationDeliveryRepository;
        this.notificationDeliveryService = notificationDeliveryService;
    }

    private NotificationDelivery createDelivery(
            Notification notification,
            NotificationChannel channel) {

        NotificationDelivery delivery = new NotificationDelivery();

        delivery.setNotification(notification);
        delivery.setChannel(channel);
        delivery.setStatus(DeliveryStatus.PENDING);

        return delivery;
    }

    @Override
    @Transactional
    public void processEvent(NotificationEvent event) {
        //Check for de-duplication
        //check in redis
        boolean isNew = redisService.isUniqueEvent(event, Duration.ofHours(24));
        if(!isNew){
            System.out.println("Duplicate event ignored");
            return;
        }

        //check in DB (if redis key got expired, beyond 24 hours)
        if(notificationRepository.existsByEventId(event.getEventId())){
            System.out.println("Duplicate event ignored");
            return;
        }

        String redisKey = "notif:" + event.getType() + ":" + event.getEventId();

        try {
            //Check for Spam (rate_limiting)
            if(!redisService.isRateLimit(event, RedisKeys.MAXLIMIT)){
                throw new EventRateLimitException("Event crossed max limit");
            }
            
            //create notification
            Notification notification = new Notification();
            notification.setEventId(event.getEventId());
            notification.setUserId(event.getUserId());
            notification.setType(event.getType());
            notification.setMessage(event.getMessage());

            Notification savedNotification = notificationRepository.save(notification);

            //create notification delivery
            NotificationDelivery emailDelivery = createDelivery(savedNotification, NotificationChannel.EMAIL);
            NotificationDelivery smsDelivery = createDelivery(savedNotification, NotificationChannel.SMS);
            NotificationDelivery pushDelivery = createDelivery(savedNotification, NotificationChannel.PUSH);

            PreferenceResponse preference =
                    NotificationPreferenceService.getPreferences(event.getUserId());

            //update status based on user preferences
            if(!preference.isEmailEnabled()){
                emailDelivery.setStatus(DeliveryStatus.SKIPPED);
                emailDelivery.setReason("User disabled this channel");
            }

            if(!preference.isSmsEnabled()){
                smsDelivery.setStatus(DeliveryStatus.SKIPPED);
                smsDelivery.setReason("User disabled this channel");
            }

            if(!preference.isPushEnabled()){
                pushDelivery.setStatus(DeliveryStatus.SKIPPED);
                pushDelivery.setReason("User disabled this channel");
            }

            //Add in DB
            notificationDeliveryRepository.save(emailDelivery);
            notificationDeliveryRepository.save(smsDelivery);
            notificationDeliveryRepository.save(pushDelivery);

            if(emailDelivery.getStatus() != DeliveryStatus.SKIPPED)
                notificationDeliveryService.processEmail(emailDelivery);

            if(smsDelivery.getStatus() != DeliveryStatus.SKIPPED)
                notificationDeliveryService.processSms(smsDelivery);

            if(pushDelivery.getStatus() != DeliveryStatus.SKIPPED)
                notificationDeliveryService.processPush(pushDelivery);

            System.out.println("All Notification sent as per User preferences");
        } catch (Exception e) {
            // Rollback fallback: If DB operations fail, remove Redis unique check key so the event can be retried
            redisService.delete(redisKey);
            throw e; // Rethrow to rollback the transaction
        }
    }
}
