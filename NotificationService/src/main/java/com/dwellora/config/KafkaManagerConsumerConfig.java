package com.dwellora.config;

import com.dwellora.event.ManagerCreatedEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * Configuration class for setting up Kafka consumer components handling manager creation events.
 */
@Configuration
public class KafkaManagerConsumerConfig {

    /**
     * Creates a {@link ConsumerFactory} configured for deserializing {@link ManagerCreatedEvent} payloads.
     */
    @Bean
    public ConsumerFactory<String, ManagerCreatedEvent> managerConsumerFactory() {
        JsonDeserializer<ManagerCreatedEvent> deserializer =
                new JsonDeserializer<>(ManagerCreatedEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "manager-notification-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a {@link ConcurrentKafkaListenerContainerFactory} for manager creation event listeners.
     */
    @Bean(name = "managerKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ManagerCreatedEvent>
    managerKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ManagerCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(managerConsumerFactory());
        return factory;
    }
}