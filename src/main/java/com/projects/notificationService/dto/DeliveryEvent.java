package com.projects.notificationService.dto;

import jakarta.validation.constraints.NotNull;

public class DeliveryEvent {
    @NotNull
    private Long deliveryId;

    public DeliveryEvent() {}

    public DeliveryEvent(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }
}
