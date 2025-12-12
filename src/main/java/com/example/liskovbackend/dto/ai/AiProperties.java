package com.example.liskovbackend.dto.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "ai.api")
public class AiProperties {
    private String url;
    private Integer timeout;
}
