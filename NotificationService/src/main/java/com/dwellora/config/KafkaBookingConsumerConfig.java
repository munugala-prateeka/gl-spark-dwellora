package com.dwellora.config;

import com.dwellora.event.BookingCreatedEvent;
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
 * Configuration class for setting up Kafka consumer components handling booking events.
 */
@Configuration
public class KafkaBookingConsumerConfig {

    /**
     * Creates a {@link ConsumerFactory} configured for deserializing {@link BookingCreatedEvent} payloads.
     */
    @Bean
    public ConsumerFactory<String, BookingCreatedEvent> bookingConsumerFactory() {
        JsonDeserializer<BookingCreatedEvent> deserializer =
                new JsonDeserializer<>(BookingCreatedEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "booking-notification-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    /**
     * Creates a {@link ConcurrentKafkaListenerContainerFactory} for booking event listeners.
     */
    @Bean(name = "bookingKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, BookingCreatedEvent>
    managerKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BookingCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(bookingConsumerFactory());
        return factory;
    }
}