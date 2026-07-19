package com.projects.notificationService.repository;

import com.projects.notificationService.entity.DeliveryDlq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryDlqRepository extends JpaRepository<DeliveryDlq, Long> {
}
