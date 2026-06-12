package com.ecommerce.commonlib.keycloak;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Keycloak admin/client configuration bound from {@code keycloak.client.*}.
 *
 * <p>No default credentials are set for the admin account — services that exercise
 * admin operations must supply {@code adminUsername} / {@code adminPassword} via
 * environment so we never ship hardcoded creds into a container.</p>
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "keycloak.client")
public class KeycloakClientProperties {

    private String serverUrl = "http://localhost:8080";
    private String realm = "ecommerce";
    private String clientId = "ecommerce-client";
    private String clientSecret;

    private String adminRealm = "master";
    private String adminClientId = "admin-cli";
    private String adminUsername;
    private String adminPassword;

    public String tokenEndpoint() {
        return serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    public String logoutEndpoint() {
        return serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
    }

    public String adminTokenEndpoint() {
        return serverUrl + "/realms/" + adminRealm + "/protocol/openid-connect/token";
    }

    public String adminUsersEndpoint() {
        return serverUrl + "/admin/realms/" + realm + "/users";
    }

    public String adminUserByIdEndpoint(String userId) {
        return adminUsersEndpoint() + "/" + userId;
    }

    public String adminRoleByNameEndpoint(String roleName) {
        return serverUrl + "/admin/realms/" + realm + "/roles/" + roleName;
    }

    public String adminUserRealmRoleMappingEndpoint(String userId) {
        return adminUserByIdEndpoint(userId) + "/role-mappings/realm";
    }
}
