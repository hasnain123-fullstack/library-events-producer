package com.learnkafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateConfig {

    public String topic = "library-events";

    @Bean
    public NewTopic libraryEvents(){

        return TopicBuilder
                .name(topic)
                .partitions(3)
                .replicas(3)
                .build();
    }
}
