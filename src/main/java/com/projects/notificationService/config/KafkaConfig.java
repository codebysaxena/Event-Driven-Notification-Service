package com.projects.notificationService.config;

import com.projects.notificationService.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic notificationEventsTopic() {
        return TopicBuilder
                .name(KafkaTopics.NOTIFICATION_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
