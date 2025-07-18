package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * Создает кастомную конфигурацию OpenAPI для документации.
     *
     * @return объект OpenAPI с настройками документации
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Management API")
                        .version("1.0.0")
                        .description("REST API для управления пользователями")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@userservice.com")
                                .url("https://userservice.com/contact")));
    }
}