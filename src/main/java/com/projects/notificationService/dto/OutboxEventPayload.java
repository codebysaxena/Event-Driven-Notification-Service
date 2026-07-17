package com.projects.notificationService.dto;

import jakarta.validation.constraints.NotNull;

public class OutboxEventPayload {
    @NotNull
    private Long deliveryId;

    public OutboxEventPayload() {}

    public OutboxEventPayload(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }
}

