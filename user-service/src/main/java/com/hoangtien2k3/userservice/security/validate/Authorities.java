package com.hoangtien2k3.userservice.security.validate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class Authorities {

//    @Value("${jwt.secret}")
    private static String jwtSecret = "vip2023";

    public static List<String> extractAuthoritiesFromToken(String token) {
        try {
            Jws<Claims> claims = Jwts
                    .parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);

            Claims body = claims.getBody();
            List<String> authorities = new ArrayList<>();
            List<Object> roles = body.get("authorities", List.class);

            for (Object role : roles) {
                if (role instanceof String) {
                    authorities.add((String) role);
                }
            }

            return authorities;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
