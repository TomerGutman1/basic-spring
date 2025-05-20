package com.handson.basic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class OpenApiConfig {                        // CHANGED: class renamed
    @Bean
    public OpenAPI BaseOpenAPI() {                  // CHANGED: return type now OpenAPI
        return new OpenAPI()
                .info(new Info()
                        .title("Hands-On Basic API") // CHANGED: add whatever metadata you like
                        .version("v1"));
    }
}