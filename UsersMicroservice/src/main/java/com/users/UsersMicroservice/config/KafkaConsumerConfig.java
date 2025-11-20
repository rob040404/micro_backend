package com.users.UsersMicroservice.config;

import com.users.UsersMicroservice.kafka.dto.FollowRequestEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, FollowRequestEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        /*
         * Assign the consumer to the consumer group called "follow-users-group".
         * Kafka uses this group to coordinate which partitions multiple consumers read and to avoid duplicates.
         */
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "follow-users-group");

        /*
         * It specifies that both the key and value of the message will be deserialized using ErrorHandlingDeserializer,
         * a decorator that allows handling deserialization errors without crashing the consumer.
         */
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        /*
         * It indicates ErrorHandlingDeserializer which deserializers should be used internally:
         * The Key as String
         * The value as JSON
         */
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        /*
         * Defines that if there is no type information in the message, the value will be assumed to be of type
         * FollowRequestEvent. This is crucial when the producer does not include
         * type metadata (for example, because USE_TYPE_INFO_HEADERS is disabled).
         */
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, FollowRequestEvent.class.getName());

        /*
         * It allows the deserializer to accept classes from any package (instead of being restricted to a safe set).
         * This is useful in development, but in production it's better to specify only trusted packages.
         */
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        /*
         * It indicates that type headers (such as __TypeId__) that the producer might have included should not be
         * relied upon. Instead, the configured default type (FollowRequestEvent) will be used, which prevents errors
         * if the headers do not match or are missing.
         */
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false); // ✅ Ignora los headers de tipo del producer

        /*
         * Create a consumer factory with the above configuration and also explicitly pass the actual deserializers
         * as arguments (key as String, value as JSON to FollowRequestEvent).
         */
        return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(),
                new JsonDeserializer<>(FollowRequestEvent.class));
    }

    /*
     * Create a concurrent listening container factory (to process multiple partitions or messages in parallel)
     * and assign the configured consumer factory to it.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FollowRequestEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FollowRequestEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
