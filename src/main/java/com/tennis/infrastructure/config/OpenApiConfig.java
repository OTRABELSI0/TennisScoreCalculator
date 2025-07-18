package com.tennis.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${tennis.openapi.dev-url:http://localhost:8080}")
    private String devUrl;

    @Value("${tennis.openapi.prod-url:http://localhost:8090}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("tennis@example.com");
        contact.setName("Tennis Team");
        contact.setUrl("https://github.com/your-username/tennis-scoring-system");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Tennis Scoring System API")
                .version("1.0.0")
                .contact(contact)
                .description("A comprehensive tennis scoring system implementing standard tennis rules including deuce scenarios.")
                .termsOfService("https://github.com/your-username/tennis-scoring-system")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}