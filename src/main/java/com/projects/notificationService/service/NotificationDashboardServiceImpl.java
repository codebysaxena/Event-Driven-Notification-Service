package com.projects.notificationService.service;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;
import com.projects.notificationService.dto.ChannelDashboardResponse;
import com.projects.notificationService.dto.DashboardResponse;
import com.projects.notificationService.dto.NotificationDetailedResponse;
import com.projects.notificationService.dto.NotificationResponse;
import com.projects.notificationService.entity.Notification;
import com.projects.notificationService.entity.NotificationDelivery;
import com.projects.notificationService.exception.NotificationNotFoundException;
import com.projects.notificationService.repository.NotificationDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationDashboardServiceImpl implements NotificationDashboardService{
    private final NotificationDeliveryRepository notificationDeliveryRepository;

    @Autowired
    public NotificationDashboardServiceImpl(NotificationDeliveryRepository notificationDeliveryRepository){
        this.notificationDeliveryRepository = notificationDeliveryRepository;
    }

    @Override
    public DashboardResponse getDashboard() {
        Long total = notificationDeliveryRepository.count();
        Long processing = notificationDeliveryRepository.countByStatus(DeliveryStatus.PROCESSING);
        Long failed = notificationDeliveryRepository.countByStatus(DeliveryStatus.FAILED);
        Long sent = notificationDeliveryRepository.countByStatus(DeliveryStatus.SENT);
        Long pending = notificationDeliveryRepository.countByStatus(DeliveryStatus.PENDING);
        Long dead = notificationDeliveryRepository.countByStatus(DeliveryStatus.DEAD);

        Long emailSent = notificationDeliveryRepository.countByChannelAndStatus(NotificationChannel.EMAIL,
                DeliveryStatus.SENT);
        Long emailFailed = notificationDeliveryRepository.countByChannelAndStatus(NotificationChannel.EMAIL,
                DeliveryStatus.FAILED);

        Long smsSent = notificationDeliveryRepository.countByChannelAndStatus(NotificationChannel.SMS,
                DeliveryStatus.SENT);
        Long smsFailed = notificationDeliveryRepository.countByChannelAndStatus(NotificationChannel.SMS,
                DeliveryStatus.FAILED);

        Long pushSent = notificationDeliveryRepository.countByChannelAndStatus(NotificationChannel.PUSH,
                DeliveryStatus.SENT);
        Long pushFailed = notificationDeliveryRepository.countByChannelAndStatus(NotificationChannel.PUSH,
                DeliveryStatus.FAILED);


        DashboardResponse response = new DashboardResponse();
        response.setTotal(total);
        response.setPending(pending);
        response.setProcessing(processing);
        response.setSent(sent);
        response.setFailed(failed);
        response.setDead(dead);

        response.setEmailSent(emailSent);
        response.setEmailFailed(emailFailed);

        response.setPushSent(pushSent);
        response.setPushFailed(pushFailed);

        response.setSmsSent(smsSent);
        response.setSmsFailed(smsFailed);
        return response;
    }

    @Override
    public ChannelDashboardResponse getDashboardChannelWise(NotificationChannel channel) {
        Long total = notificationDeliveryRepository.countByChannel(channel);
        Long processing = notificationDeliveryRepository.countByChannelAndStatus(channel, DeliveryStatus.PROCESSING);
        Long failed = notificationDeliveryRepository.countByChannelAndStatus(channel, DeliveryStatus.FAILED);
        Long sent = notificationDeliveryRepository.countByChannelAndStatus(channel, DeliveryStatus.SENT);
        Long pending = notificationDeliveryRepository.countByChannelAndStatus(channel, DeliveryStatus.PENDING);
        Long dead = notificationDeliveryRepository.countByChannelAndStatus(channel, DeliveryStatus.DEAD);

        ChannelDashboardResponse response = new ChannelDashboardResponse();
        response.setChannel(channel);
        response.setTotal(total);
        response.setPending(pending);
        response.setProcessing(processing);
        response.setSent(sent);
        response.setFailed(failed);
        response.setDead(dead);

        return response;
    }

    @Override
    public NotificationDetailedResponse getDetailedNotificationInfo(Long id) {
        NotificationDelivery delivery = notificationDeliveryRepository.findById(id).orElse(null);
        if(delivery == null){
            throw new NotificationNotFoundException("Notification delivery not found: " + id);
        }

        Notification notification = delivery.getNotification();

        NotificationDetailedResponse response = new NotificationDetailedResponse();
        response.setNotificationId(notification.getId());
        response.setDeliveryId(delivery.getId());
        response.setEventId(notification.getEventId());
        response.setUserId(notification.getUserId());
        response.setChannel(delivery.getChannel());
        response.setReason(delivery.getReason());
        response.setStatus(delivery.getStatus());
        response.setMessage(notification.getMessage());
        response.setRetryCount(delivery.getRetryCount());
        response.setCreatedAt(delivery.getCreatedAt());
        response.setUpdatedAt(delivery.getUpdatedAt());
        return response;
    }

    @Override
    public List<NotificationResponse> getNotificationsByChannel(NotificationChannel channel) {
        List<NotificationDelivery> deliveryList = notificationDeliveryRepository.findByChannel(channel);
        List<NotificationResponse> responseList = new ArrayList<>();

        for(NotificationDelivery delivery: deliveryList){
            responseList.add(mapToResponse(delivery));
        }
        return responseList;
    }

    @Override
    public List<NotificationResponse> getNotificationsByStatus(DeliveryStatus status) {
        List<NotificationDelivery> deliveryList = notificationDeliveryRepository.findByStatus(status);
        List<NotificationResponse> responseList = new ArrayList<>();

        for(NotificationDelivery delivery: deliveryList){
            responseList.add(mapToResponse(delivery));
        }
        return responseList;
    }

    @Override
    public List<NotificationResponse> getNotificationsByChannelAndStatus(NotificationChannel channel, DeliveryStatus status) {
        List<NotificationDelivery> deliveryList = notificationDeliveryRepository.findByChannelAndStatus(channel, status);
        List<NotificationResponse> responseList = new ArrayList<>();

        for(NotificationDelivery delivery: deliveryList){
            responseList.add(mapToResponse(delivery));
        }
        return responseList;
    }

     private NotificationResponse mapToResponse(NotificationDelivery delivery){
        Notification notification = delivery.getNotification();
        NotificationResponse response = new NotificationResponse();

        response.setDeliveryId(delivery.getId());
        response.setNotificationId(notification.getId());
        response.setChannel(delivery.getChannel());
        response.setStatus(delivery.getStatus());
        response.setReason(delivery.getReason());
        response.setRetryCount(delivery.getRetryCount());
        response.setCreatedAt(delivery.getCreatedAt());

        return response;
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        List<NotificationDelivery> deliveryList = notificationDeliveryRepository.findAll();

        List<NotificationResponse> responseList = new ArrayList<>();

        for(NotificationDelivery delivery : deliveryList){
            responseList.add(mapToResponse(delivery));
        }
        return responseList;
    }

    @Override
    public List<NotificationResponse> getNotifications
            (DeliveryStatus status, NotificationChannel channel) {

        if(status != null && channel != null){
            return getNotificationsByChannelAndStatus(channel, status);
        }

        if(status != null){
            return getNotificationsByStatus(status);
        }

        if(channel != null){
            return getNotificationsByChannel(channel);
        }

        return getAllNotifications();
    }
}
