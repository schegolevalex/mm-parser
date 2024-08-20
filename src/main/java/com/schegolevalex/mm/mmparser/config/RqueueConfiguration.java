package com.schegolevalex.mm.mmparser.config;


import com.github.sonus21.rqueue.config.SimpleRqueueListenerContainerFactory;
import io.lettuce.core.ReadFrom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RqueueConfiguration {
    // this property must be set to true if you're using webflux or reactive redis
    @Value("${rqueue.reactive.enabled:false}")
    private boolean reactiveEnabled;

    @Bean
    public SimpleRqueueListenerContainerFactory simpleRqueueListenerContainerFactory() {
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.MASTER_PREFERRED)
                .build();

        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();

        LettuceConnectionFactory redisConnectionFactory = new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
        redisConnectionFactory.afterPropertiesSet();

        SimpleRqueueListenerContainerFactory factory = new SimpleRqueueListenerContainerFactory();
//        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
//        threadPoolTaskExecutor.setThreadNamePrefix("taskExecutor");
//        threadPoolTaskExecutor.setCorePoolSize(1);
//        threadPoolTaskExecutor.setMaxPoolSize(2);
//        threadPoolTaskExecutor.setQueueCapacity(0);
//        threadPoolTaskExecutor.afterPropertiesSet();

//        factory.setTaskExecutor(threadPoolTaskExecutor);
        factory.setRedisConnectionFactory(redisConnectionFactory);
//        factory.setMessageConverterProvider(MappingJackson2MessageConverter::new);

        if (reactiveEnabled)
            factory.setReactiveRedisConnectionFactory(redisConnectionFactory);

        return factory;
    }
}

