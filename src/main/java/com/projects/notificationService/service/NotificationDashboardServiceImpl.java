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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<NotificationResponse> getNotificationsByChannel(NotificationChannel channel, Pageable pageable) {
        Page<NotificationDelivery> deliveryList =
                notificationDeliveryRepository.findByChannel(channel, pageable);

        Page<NotificationResponse> deliveryPage = deliveryList.map(this::mapToResponse);
        return deliveryPage;
    }

    @Override
    public Page<NotificationResponse> getNotificationsByStatus(DeliveryStatus status, Pageable pageable) {
        Page<NotificationDelivery> deliveryList = notificationDeliveryRepository.
                findByStatus(status, pageable);

        Page<NotificationResponse> deliveryPage = deliveryList.map(this::mapToResponse);
        return deliveryPage;
    }

    @Override
    public Page<NotificationResponse> getNotificationsByChannelAndStatus(NotificationChannel channel, DeliveryStatus status,
                                                                         Pageable paginationObject) {
        Page<NotificationDelivery> deliveryList = notificationDeliveryRepository.
                findByChannelAndStatus(channel, status, paginationObject);

        Page<NotificationResponse> deliveryPage = deliveryList.map(this::mapToResponse);
        return deliveryPage;
    }

    @Override
    public Page<NotificationResponse> getAllNotifications(Pageable paginationObject) {
        Page<NotificationDelivery> notificationPage = notificationDeliveryRepository.
                findAll(paginationObject);

        Page<NotificationResponse> deliveryPage = notificationPage.map(this::mapToResponse);

        return deliveryPage;
    }

    @Override
    public Page<NotificationResponse> getNotifications
            (DeliveryStatus status, NotificationChannel channel,
             int page, int size, String sortParam) {

        Pageable paginationObject = buildPageable(page, size, sortParam);

        if(status != null && channel != null){
            return getNotificationsByChannelAndStatus(channel, status, paginationObject);
        }

        if(status != null){
            return getNotificationsByStatus(status, paginationObject);
        }

        if(channel != null){
            return getNotificationsByChannel(channel, paginationObject);
        }

        return getAllNotifications(paginationObject);
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

    private Pageable buildPageable(int page, int size, String sortParam){
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        Sort sortObj;
        if (sortParam != null && !sortParam.trim().isEmpty()) {
            if (sortParam.contains(",")) {
                String[] parts = sortParam.split(",");
                String sortField = parts[0].trim();       // "createdAt"
                String sortDirection = parts.length > 1 ? parts[1].trim() : "asc";

                sortObj = sortDirection.equalsIgnoreCase("desc") ?
                        Sort.by(sortField).descending() :
                        Sort.by(sortField).ascending();
            } else {
                // Fallback if the user just passes a field name like "sort=createdAt"
                sortObj = Sort.by(sortParam.trim()).ascending();
            }
        } else {
            // Default sort if sortParam is null or empty
            sortObj = Sort.by("createdAt").descending();
        }

        // 0. Create Pageable object with page and size (sorting is optional)
        Pageable paginationObject = PageRequest.of(page, size, sortObj);
        return paginationObject;
    }
}
