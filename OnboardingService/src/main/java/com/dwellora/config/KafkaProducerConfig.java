package com.dwellora.config;

import com.dwellora.event.CommunityApprovedEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Kafka producer configuration for publishing community approved events.
 */
@Configuration
public class KafkaProducerConfig {

    /**
     * Configures and creates the Kafka producer factory instance.
     */
    @Bean
    public ProducerFactory<String, CommunityApprovedEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Configures and creates the Kafka template for sending messages.
     */
    @Bean
    public KafkaTemplate<String, CommunityApprovedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}