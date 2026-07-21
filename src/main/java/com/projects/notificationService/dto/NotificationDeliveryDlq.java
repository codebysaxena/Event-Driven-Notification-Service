package com.projects.notificationService.dto;
import com.projects.notificationService.constants.NotificationChannel;

public class NotificationDeliveryDlq {
    private Long deliveryId;
    private NotificationChannel channel;
    private String reason;
    private Integer retryCount;
    private String deadAt;

    public NotificationDeliveryDlq() {}

    public NotificationDeliveryDlq(Long deliveryId, NotificationChannel channel, String reason,
                                   Integer retryCount, String deadAt) {
        this.deliveryId = deliveryId;
        this.channel = channel;
        this.reason = reason;
        this.retryCount = retryCount;
        this.deadAt = deadAt;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
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

    public String getDeadAt() {
        return deadAt;
    }

    public void setDeadAt(String deadAt) {
        this.deadAt = deadAt;
    }
}
