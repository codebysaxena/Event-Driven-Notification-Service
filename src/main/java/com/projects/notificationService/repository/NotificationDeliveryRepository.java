package com.projects.notificationService.repository;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;
import com.projects.notificationService.entity.NotificationDelivery;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {
    List<NotificationDelivery> findAllByNotification_Id(Long notificationId);

    List<NotificationDelivery> findTop100ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(
            DeliveryStatus status,
            Integer retryCount
    );

    List<NotificationDelivery> findByStatusAndUpdatedAtBefore
            (DeliveryStatus status, LocalDateTime timeoutTime);

    //Add methods for metrics dashboard
    long count();
    long countByStatus(DeliveryStatus status);
    long countByChannel(NotificationChannel channel);
    long countByChannelAndStatus(NotificationChannel channel, DeliveryStatus status);

    //add pagination and sorting
    Page<NotificationDelivery> findAll(@NonNull Pageable pageable);

    Page<NotificationDelivery> findByStatus(DeliveryStatus status, Pageable pageable);

    Page<NotificationDelivery> findByChannel(NotificationChannel channel, Pageable pageable);

    Page<NotificationDelivery> findByChannelAndStatus(
            NotificationChannel channel,
            DeliveryStatus status,
            Pageable pageable
    );
}
