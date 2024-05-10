package com.schegolevalex.mm.mmparser.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram-bot")
@Setter
@Getter
public class BotConfiguration {
    private String botUsername;
    private Long creatorId;
    private String botToken;
}
