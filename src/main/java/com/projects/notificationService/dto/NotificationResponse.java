package com.projects.notificationService.dto;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long deliveryId;
    private Long notificationId;
    private NotificationChannel channel;
    private DeliveryStatus status;
    private String reason;
    private Integer retryCount;
    private LocalDateTime createdAt;

    public NotificationResponse() {}

    public NotificationResponse(Long deliveryId, Long notificationId, NotificationChannel channel,
                                DeliveryStatus status, String reason, Integer retryCount,
                                LocalDateTime createdAt) {
        this.deliveryId = deliveryId;
        this.notificationId = notificationId;
        this.channel = channel;
        this.status = status;
        this.reason = reason;
        this.retryCount = retryCount;
        this.createdAt = createdAt;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
