package com.projects.notificationService.repository;

import com.projects.notificationService.entity.NotificationDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {
    List<NotificationDelivery> findAllByNotification_Id(Long notificationId);
}
