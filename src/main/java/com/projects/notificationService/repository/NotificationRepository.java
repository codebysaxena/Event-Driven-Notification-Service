package com.projects.notificationService.repository;

import com.projects.notificationService.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    boolean existsByEventId(String eventId);

    Optional<Notification> findByEventId(String eventId);

    List<Notification> findByUserId(Long userId);
}
