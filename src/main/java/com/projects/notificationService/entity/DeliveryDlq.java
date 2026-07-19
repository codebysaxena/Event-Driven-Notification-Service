package com.projects.notificationService.entity;

import com.projects.notificationService.constants.NotificationChannel;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_dlq")
public class DeliveryDlq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "delivery_id")
    private Long deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private NotificationChannel channel;

    @Column(name = "reason")
    private String reason;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "dead_at")
    private LocalDateTime deadAt;

    public DeliveryDlq() {}

    public DeliveryDlq(Long deliveryId, NotificationChannel channel, String reason,
                       Integer retryCount, LocalDateTime deadAt) {
        this.deliveryId = deliveryId;
        this.channel = channel;
        this.reason = reason;
        this.retryCount = retryCount;
        this.deadAt = deadAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getDeadAt() {
        return deadAt;
    }

    public void setDeadAt(LocalDateTime deadAt) {
        this.deadAt = deadAt;
    }

    @PrePersist
    public void onCreate() {
        if(deadAt == null){
            deadAt = LocalDateTime.now();
        }
    }
}
