package com.users.UsersMicroservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Class with Redis caching configuration
 *
 * <p>This configuration:
 * - Uses JSON serialization for cache values (instead of Java serialization),
 * - Sets a global time-to-live (TTL) of 2 hours for all cached entries,
 * - Prevents caching of {@code null} values (which avoids unnecessary cache pollution),
 * - Enables integration with Spring's {@code @Cacheable}, {@code @CacheEvict}, etc.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Defines the default Redis cache configuration applied to all caches unless overridden.
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                // Sets expiration: cached data is automatically removed after 2 hours
                .entryTtl(Duration.ofHours(2))
                // Prevents caching of null results (e.g., when a user is not found)
                .disableCachingNullValues()
                // Serializes cached objects as JSON (instead of opaque binary Java serialization)
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );
    }
}
