package com.example.dine_in_order_api.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class CloudinaryConfig {

    private final AppEnv appEnv;

    @Bean
    Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", appEnv.getCloudinary().getCloudName(),
                "api_key", appEnv.getCloudinary().getApiKey(),
                "api_secret", appEnv.getCloudinary().getApiSecret()
        ));
    }
}
