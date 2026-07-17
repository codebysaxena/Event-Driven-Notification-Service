package com.projects.notificationService.constants;

public enum OutboxEventStatus {
    PENDING,
    PROCESSING,
    SENT,
    FAILED,
    DEAD
}
