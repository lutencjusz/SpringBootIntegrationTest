package com.example.integrationTest;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerApiConfig {
    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Testowanie integracyjne API z JWT");

        io.swagger.v3.oas.models.info.Contact myContact = new Contact();
        myContact.setName("Michał Sobieraj");
        myContact.setEmail("michal.sobieraj@sopim.pl");

        io.swagger.v3.oas.models.info.Info information = new Info()
                .title("System do testowania integracyjnego API z JWT")
                .version("1.0")
                .description("To są operacje REST API dla testowania integracyjnego z użyciem JWT")
                .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                .termsOfService("http://swagger.io/terms/")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }
}
