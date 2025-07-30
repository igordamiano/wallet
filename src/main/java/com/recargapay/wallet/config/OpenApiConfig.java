package com.recargapay.wallet.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@OpenAPIDefinition(
        info = @Info(
                title = "RecargaPay Wallet API",
                version = "1.0",
                description = "REST API for digital wallet operations developed for the RecargaPay backend challenge"
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
public class OpenApiConfig {
}
