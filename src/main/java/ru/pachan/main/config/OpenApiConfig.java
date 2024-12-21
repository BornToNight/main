package ru.pachan.main.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.local-url}")
    private String localUrl;

    @Value("${openapi.dev-url}")
    private String devUrl;

    @Bean
    OpenAPI myOpenAPI() {
        Server localServer = new Server().url(localUrl).description("Адрес локального сервера");
        Server devServer = new Server().url(devUrl).description("Адрес dev сервера");

        Info info = new Info().title("API документация Пет проекта");

        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("bearerAuth")).components(
                new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
        ).info(info).servers(new ArrayList<>() {{
            add(localServer);
            add(devServer);
        }});
    }

}
