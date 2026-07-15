package com.projects.notificationService.exception;

public class EventRateLimitException extends RuntimeException {
    public EventRateLimitException(String message) {
        super(message);
    }
}
