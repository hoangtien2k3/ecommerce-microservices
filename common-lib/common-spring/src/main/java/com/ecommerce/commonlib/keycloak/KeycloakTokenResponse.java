package com.ecommerce.commonlib.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Snake-case payload returned by Keycloak's {@code /protocol/openid-connect/token} endpoint.
 * Using a record gives us immutability + concise binding.
 */
public record KeycloakTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Long expiresIn,
        @JsonProperty("refresh_expires_in") Long refreshExpiresIn,
        @JsonProperty("scope") String scope
) {
}
