package com.schegolevalex.mm.mmparser.config;

import com.schegolevalex.mm.mmparser.parser.Parser;
import com.schegolevalex.mm.mmparser.parser.ParserFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class ParserPoolConfig {

    private final ParserFactory parserFactory;

    @Bean
    public GenericObjectPool<Parser> parserPool() {
        GenericObjectPoolConfig<Parser> config = new GenericObjectPoolConfig<>();
        config.setMinIdle(1);
        config.setMaxTotal(4);
        config.setMaxWait(Duration.ofSeconds(30));
        config.setJmxEnabled(false);

        return new GenericObjectPool<>(parserFactory, config);
    }
}
