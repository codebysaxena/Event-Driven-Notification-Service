package com.projects.notificationService.service;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;
import com.projects.notificationService.dto.ChannelDashboardResponse;
import com.projects.notificationService.dto.DashboardResponse;
import com.projects.notificationService.dto.NotificationResponse;
import com.projects.notificationService.dto.NotificationDetailedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationDashboardService {
    DashboardResponse getDashboard();
    ChannelDashboardResponse getDashboardChannelWise(NotificationChannel channel);
    NotificationDetailedResponse getDetailedNotificationInfo(Long id);
    Page<NotificationResponse> getNotificationsByChannel(NotificationChannel channel, Pageable pageable);
    Page<NotificationResponse> getNotificationsByStatus(DeliveryStatus status, Pageable pageable);
    Page<NotificationResponse> getNotificationsByChannelAndStatus
            (NotificationChannel channel, DeliveryStatus status, Pageable pageable);

    Page<NotificationResponse> getNotifications
            (DeliveryStatus status, NotificationChannel channel, int page, int size, String sortParam);

    Page<NotificationResponse> getAllNotifications(Pageable pageable);
}
