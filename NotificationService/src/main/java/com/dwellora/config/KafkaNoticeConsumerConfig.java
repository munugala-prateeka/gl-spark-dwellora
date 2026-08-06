package com.dwellora.config;

import com.dwellora.event.NoticePublishedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaNoticeConsumerConfig {

    @Bean
    public ConsumerFactory<String, NoticePublishedEvent> noticeConsumerFactory() {
        JsonDeserializer<NoticePublishedEvent> deserializer =
                new JsonDeserializer<>(NoticePublishedEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean(name = "noticeKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, NoticePublishedEvent>
    noticeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NoticePublishedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(noticeConsumerFactory());
        return factory;
    }
}