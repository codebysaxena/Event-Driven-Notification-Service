package com.projects.notificationService.controller;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;
import com.projects.notificationService.dto.ChannelDashboardResponse;
import com.projects.notificationService.dto.DashboardResponse;
import com.projects.notificationService.dto.NotificationDetailedResponse;
import com.projects.notificationService.dto.NotificationResponse;
import com.projects.notificationService.service.NotificationDashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<NotificationResponse> getNotifications(
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) NotificationChannel channel) {

        return dashboardService.getNotifications(status, channel);
    }
}
