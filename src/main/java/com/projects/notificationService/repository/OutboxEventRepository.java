package com.projects.notificationService.repository;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.OutboxEventStatus;
import com.projects.notificationService.entity.NotificationDelivery;
import com.projects.notificationService.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findTop100ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(
            Collection<OutboxEventStatus> statuses,
            Integer retryCount
    );

    List<OutboxEvent> findByStatusAndUpdatedAtBefore
            (OutboxEventStatus status, LocalDateTime timeoutTime);

    List<OutboxEvent> findByStatus(OutboxEventStatus status);
}
