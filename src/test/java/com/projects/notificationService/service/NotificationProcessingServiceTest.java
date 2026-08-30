package com.projects.notificationService.service;

import com.projects.notificationService.dto.NotificationEvent;
import com.projects.notificationService.dto.PreferenceResponse;
import com.projects.notificationService.entity.Notification;
import com.projects.notificationService.exception.EventRateLimitException;
import com.projects.notificationService.repository.NotificationDeliveryRepository;
import com.projects.notificationService.repository.NotificationRepository;
import com.projects.notificationService.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationProcessingServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationPreferenceService notificationPreferenceService;

    @Mock
    private RedisService redisService;

    @Mock
    private NotificationDeliveryRepository notificationDeliveryRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    private NotificationProcessingServiceImpl notificationProcessingService;

    @BeforeEach
    void setUp() {
        notificationProcessingService = new NotificationProcessingServiceImpl(
                notificationRepository,
                notificationPreferenceService,
                redisService,
                notificationDeliveryRepository,
                outboxEventRepository
        );
    }

    @Test
    @DisplayName("Should drop duplicate event when Redis isUniqueEvent returns false")
    void testProcessEvent_WhenDuplicateInRedis_ShouldDropEvent() {
        NotificationEvent event = new NotificationEvent("evt-101", "Payment Failed", "PAYMENT_FAILED", 1L);
        when(redisService.isUniqueEvent(any(NotificationEvent.class), any(Duration.class))).thenReturn(false);

        notificationProcessingService.processEvent(event);

        verify(notificationRepository, never()).save(any(Notification.class));
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should drop duplicate event when MySQL existsByEventId returns true")
    void testProcessEvent_WhenDuplicateInDatabase_ShouldDropEvent() {
        NotificationEvent event = new NotificationEvent("evt-101", "Payment Failed", "PAYMENT_FAILED", 1L);
        when(redisService.isUniqueEvent(any(NotificationEvent.class), any(Duration.class))).thenReturn(true);
        when(notificationRepository.existsByEventId("evt-101")).thenReturn(true);

        notificationProcessingService.processEvent(event);

        verify(notificationRepository, never()).save(any(Notification.class));
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should save notification and outbox when event is valid and allowed")
    void testProcessEvent_WhenValidEvent_ShouldSaveNotificationAndOutbox() {
        NotificationEvent event = new NotificationEvent("evt-102", "Order Placed", "ORDER_PLACED", 1L);
        when(redisService.isUniqueEvent(any(NotificationEvent.class), any(Duration.class))).thenReturn(true);
        when(notificationRepository.existsByEventId("evt-102")).thenReturn(false);
        when(redisService.isRateLimit(any(NotificationEvent.class), anyInt())).thenReturn(true);
        when(notificationPreferenceService.getPreferences(1L)).thenReturn(new PreferenceResponse(true, true, false));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notificationProcessingService.processEvent(event);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(notificationDeliveryRepository, times(3)).save(any()); // Email, SMS, Push
        verify(outboxEventRepository, times(2)).save(any()); // Email and SMS outbox events (Push disabled)
    }

    @Test
    @DisplayName("Should throw EventRateLimitException when user exceeds rate limit")
    void testProcessEvent_WhenRateLimitExceeded_ShouldThrowException() {
        NotificationEvent event = new NotificationEvent("evt-103", "Spam Promo", "PROMO", 1L);
        when(redisService.isUniqueEvent(any(NotificationEvent.class), any(Duration.class))).thenReturn(true);
        when(notificationRepository.existsByEventId("evt-103")).thenReturn(false);
        when(redisService.isRateLimit(any(NotificationEvent.class), anyInt())).thenReturn(false);

        assertThrows(EventRateLimitException.class, () -> notificationProcessingService.processEvent(event));
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}
