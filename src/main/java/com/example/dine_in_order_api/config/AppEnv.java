package com.example.dine_in_order_api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppEnv {
    private String baseUrl;
    private Cloudinary cloudinary;
    private Security security;
    private Domain domain;

    @Getter
    @Setter
    public static class Domain {
        private String name;
        private boolean secure;
        private String sameSite;
    }

    @Getter
    @Setter
    public static class Cloudinary {
        private String apiKey;
        private String apiSecret;
        private String cloudName;
    }

    @Getter
    @Setter
    public static class Security{
        private String secret;
        private TokenValidity tokenValidity;

        @Getter
        @Setter
        public static class TokenValidity{
            private long accessValidity;
            private long refreshValidity;
        }
    }
}
