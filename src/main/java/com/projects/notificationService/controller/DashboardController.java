package com.projects.notificationService.controller;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;
import com.projects.notificationService.dto.ChannelDashboardResponse;
import com.projects.notificationService.dto.DashboardResponse;
import com.projects.notificationService.dto.NotificationDetailedResponse;
import com.projects.notificationService.dto.NotificationResponse;
import com.projects.notificationService.service.NotificationDashboardService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class DashboardController {
    private final NotificationDashboardService dashboardService;

    public DashboardController(NotificationDashboardService dashboardService){
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse getNotificationInfo(){
        return dashboardService.getDashboard();
    }

    @GetMapping("/dashboard/channel/{channel}")
    public ChannelDashboardResponse getNotificationInfoChannelWise
            (@PathVariable NotificationChannel channel){
        return dashboardService.getDashboardChannelWise(channel);
    }

    @GetMapping("/notifications/{id}")
    public NotificationDetailedResponse getDetailedNotificationInfo(
            @PathVariable Long id) {

        return dashboardService.getDetailedNotificationInfo(id);
    }

    @GetMapping("/notifications")
    public Page<NotificationResponse> getNotifications(
            @RequestParam(name = "status", required = false) DeliveryStatus status,
            @RequestParam(name = "channel", required = false) NotificationChannel channel,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "2") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt,desc") String sortParam) {

        return dashboardService.getNotifications(status, channel, page, size, sortParam);
    }
}
