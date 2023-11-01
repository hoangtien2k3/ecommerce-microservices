package com.hoangtien2k3.orderservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

public class AuthorityTokenUtil {

    @Value("${jwt.secret-key}")
    private String secret;

    public boolean checkPermission(String token, String requiredRole) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            String role = claims.get("authorities", String.class);
            return requiredRole.equals(role);
        } catch (Exception e) {
            return false;
        }
    }
    
}
