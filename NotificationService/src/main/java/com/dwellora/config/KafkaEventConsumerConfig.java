package com.dwellora.config;

import com.dwellora.event.EventCreatedEvent;
import com.dwellora.event.RsvpCancelledEvent;
import com.dwellora.event.RsvpConfirmedEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * Configuration class for setting up Kafka consumer components handling event and RSVP notification messages.
 */
@EnableKafka
@Configuration
public class KafkaEventConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    /**
     * Creates a {@link ConsumerFactory} configured for deserializing {@link EventCreatedEvent} payloads.
     */
    @Bean
    public ConsumerFactory<String, EventCreatedEvent> eventConsumerFactory() {
        JsonDeserializer<EventCreatedEvent> deserializer =
                new JsonDeserializer<>(EventCreatedEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-event-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a {@link ConcurrentKafkaListenerContainerFactory} for event creation listeners.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventCreatedEvent>
    eventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EventCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(eventConsumerFactory());
        return factory;
    }

    /**
     * Creates a {@link ConsumerFactory} configured for deserializing {@link RsvpConfirmedEvent} payloads.
     */
    @Bean
    public ConsumerFactory<String, RsvpConfirmedEvent> rsvpConsumerFactory() {
        JsonDeserializer<RsvpConfirmedEvent> deserializer =
                new JsonDeserializer<>(RsvpConfirmedEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-rsvp-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a {@link ConcurrentKafkaListenerContainerFactory} for RSVP confirmation listeners.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RsvpConfirmedEvent>
    rsvpKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RsvpConfirmedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(rsvpConsumerFactory());
        return factory;
    }

    /**
     * Creates a {@link ConsumerFactory} configured for deserializing {@link RsvpCancelledEvent} payloads.
     */
    @Bean
    public ConsumerFactory<String, RsvpCancelledEvent> rsvpCancelledConsumerFactory() {
        JsonDeserializer<RsvpCancelledEvent> deserializer =
                new JsonDeserializer<>(RsvpCancelledEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-rsvp-cancelled-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a {@link ConcurrentKafkaListenerContainerFactory} for RSVP cancellation listeners.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RsvpCancelledEvent>
    rsvpCancelledKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RsvpCancelledEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(rsvpCancelledConsumerFactory());
        return factory;
    }
}