package com.projects.notificationService.dto;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;

import java.time.LocalDateTime;

public class NotificationDetailedResponse {
    private Long notificationId;
    private Long deliveryId;
    private String eventId;
    private Long userId;
    private NotificationChannel channel;
    private DeliveryStatus status;
    private String reason;
    private Integer retryCount;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NotificationDetailedResponse() {}

    public NotificationDetailedResponse(Long notificationId, Long deliveryId, String eventId, Long userId,
                                        NotificationChannel channel, DeliveryStatus status, String reason,
                                        Integer retryCount, String message, LocalDateTime createdAt,
                                        LocalDateTime updatedAt) {
        this.notificationId = notificationId;
        this.deliveryId = deliveryId;
        this.eventId = eventId;
        this.userId = userId;
        this.channel = channel;
        this.status = status;
        this.reason = reason;
        this.retryCount = retryCount;
        this.message = message;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
