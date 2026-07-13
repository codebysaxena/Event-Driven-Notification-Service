package com.projects.notificationService.repository;

import com.projects.notificationService.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    Optional<NotificationPreference> findPreferencesByUserId(Long userId);
    Optional<NotificationPreference> findByUserEmail(String email);
}
