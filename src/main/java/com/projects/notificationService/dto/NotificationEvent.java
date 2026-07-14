package com.projects.notificationService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationEvent {
    @NotBlank(message = "eventId is required")
    private String eventId;

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "type is required")
    private String type;

    @NotBlank(message = "message is required")
    private String message;

    public NotificationEvent() {}

    public NotificationEvent(String eventId, String message, String type, Long userId) {
        this.eventId = eventId;
        this.message = message;
        this.type = type;
        this.userId = userId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
