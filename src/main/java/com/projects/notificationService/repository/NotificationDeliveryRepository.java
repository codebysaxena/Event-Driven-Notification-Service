package com.projects.notificationService.repository;

import com.projects.notificationService.constants.DeliveryStatus;
import com.projects.notificationService.constants.NotificationChannel;
import com.projects.notificationService.entity.NotificationDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {
    List<NotificationDelivery> findAllByNotification_Id(Long notificationId);

    List<NotificationDelivery> findByStatus(DeliveryStatus status);

    List<NotificationDelivery> findByChannelAndStatus(
            NotificationChannel channel,
            DeliveryStatus status
    );

    List<NotificationDelivery> findTop100ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(
            DeliveryStatus status,
            Integer retryCount
    );
}
