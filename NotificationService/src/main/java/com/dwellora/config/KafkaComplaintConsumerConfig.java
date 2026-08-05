package com.dwellora.config;

import com.dwellora.event.ComplaintUpdatedEvent;
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

@Configuration
public class KafkaComplaintConsumerConfig {

    @Bean
    public ConsumerFactory<String, ComplaintUpdatedEvent> complaintConsumerFactory() {
        JsonDeserializer<ComplaintUpdatedEvent> deserializer =
                new JsonDeserializer<>(ComplaintUpdatedEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean(name = "complaintKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ComplaintUpdatedEvent>
    complaintKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ComplaintUpdatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(complaintConsumerFactory());
        return factory;
    }
}