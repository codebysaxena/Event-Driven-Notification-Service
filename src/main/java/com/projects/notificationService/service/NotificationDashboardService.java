package com.projects.notificationService.service;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;
import com.projects.notificationService.dto.ChannelDashboardResponse;
import com.projects.notificationService.dto.DashboardResponse;
import com.projects.notificationService.dto.NotificationResponse;
import com.projects.notificationService.dto.NotificationDetailedResponse;

import java.util.List;

public interface NotificationDashboardService {
    DashboardResponse getDashboard();
    ChannelDashboardResponse getDashboardChannelWise(NotificationChannel channel);
    NotificationDetailedResponse getDetailedNotificationInfo(Long id);
    List<NotificationResponse> getNotificationsByChannel(NotificationChannel channel);
    List<NotificationResponse> getNotificationsByStatus(DeliveryStatus status);
    List<NotificationResponse> getNotificationsByChannelAndStatus
            (NotificationChannel channel, DeliveryStatus status);

    List<NotificationResponse> getNotifications
            (DeliveryStatus status, NotificationChannel channel);

    List<NotificationResponse> getAllNotifications();
}
