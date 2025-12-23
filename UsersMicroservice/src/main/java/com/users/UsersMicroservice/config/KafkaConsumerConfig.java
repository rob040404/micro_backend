package com.users.UsersMicroservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for kafka's consumer
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Inyecta tu KafkaTemplate (debe estar definido como @Bean en otra config)
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaConsumerConfig(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
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


        // Enable type of info from headers (sent by producer)
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true);

        // Map alias "followRequest" to concrete event classes
        config.put(JsonDeserializer.TYPE_MAPPINGS,
                "followRequest:com.users.UsersMicroservice.kafka.dto.FollowRequestEventRequestDTO," +
                        "followAccepted:com.users.UsersMicroservice.kafka.dto.FollowAnsweredEventRequestDTO");

        // Restrict trusted packages (security best practice)
        config.put(JsonDeserializer.TRUSTED_PACKAGES,
                "com.users.UsersMicroservice.kafka.dto");

        /*
         * No specified type, it gets resolved by headers
         */
        return new DefaultKafkaConsumerFactory<>(config);

        /*return new DefaultKafkaConsumerFactory<>(config,
                new StringDeserializer(),
                new JsonDeserializer<>());

         */
    }

    /*
     * Create a concurrent listening container factory (to process multiple partitions or messages in parallel)
     * and assign the configured consumer factory to it.
     */

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());


        // Retry + dead-letter on failure
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate),
                new FixedBackOff(2000L, 3) // 3 retries, 2s between them
        ));

        return factory;
    }
}
