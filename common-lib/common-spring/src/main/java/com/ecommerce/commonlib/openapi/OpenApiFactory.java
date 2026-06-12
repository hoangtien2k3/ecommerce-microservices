package com.ecommerce.commonlib.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Builder for the platform {@link OpenAPI} bean. Bearer-JWT auth is wired in unconditionally
 * because every service is a JWT resource-server; OpenAPI metadata comes from
 * {@link OpenApiProperties} so each service personalizes its docs without redefining the bean.
 */
public final class OpenApiFactory {

    private static final String BEARER_AUTH = "bearerAuth";

    private OpenApiFactory() {
    }

    public static OpenAPI build(OpenApiProperties props) {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components().addSecuritySchemes(BEARER_AUTH,
                        new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title(props.title())
                        .version(props.version())
                        .description(props.description()));
    }
}
