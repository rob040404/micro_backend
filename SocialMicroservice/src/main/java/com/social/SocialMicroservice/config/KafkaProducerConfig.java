package com.social.SocialMicroservice.config;

import com.social.SocialMicroservice.kafka.dto.FollowRequestEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    //Stores the Kafka servers specified in .properties
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    //Generic fabric
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        //Establishes to which Kafka brokers the Producer should connect
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //The key of the Kafka message will be serialized as a String
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //The value oth the Kafka message will be serialized as a JSON
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        //Includes metha data of types in the header. Example: every message will include aheader liek: __TypeId__: "followRequest"
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);

        /*
         * Establishes an alias (followRequest) for the full class FollowRequestEvent
         * so when the message is serialized to JSON this short key (followRequest) will be included instead of the whole name
         * so other consumers can map correctly the type without using the FQDN (Fully Qualified Class Name).
         */
        config.put(JsonSerializer.TYPE_MAPPINGS,
                "followRequest:com.social.SocialMicroservice.kafka.dto.FollowRequestEvent," +
                "followAccepted:com.social.SocialMicroservice.kafka.dto.FollowAnsweredEvent");

        //Creates a reusable fabric that will serve to create instances of producers
        return new DefaultKafkaProducerFactory<>(config);
    }

    //Creates a KafkaTemplate, a wrapper that is used to send messages to topics using the configures fabric
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
