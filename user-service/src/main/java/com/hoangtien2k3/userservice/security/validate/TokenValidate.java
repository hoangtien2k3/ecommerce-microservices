package com.hoangtien2k3.userservice.security.validate;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenValidate {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public boolean validateToken(String token) {
        if (SECRET_KEY == null || SECRET_KEY.isEmpty())
            throw new IllegalArgumentException("Not found secret key in structure");

        if (token.startsWith("Bearer "))
            token = token.replace("Bearer ", "");

        try {
            Claims claims = Jwts
                    .parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            long currentTimeMillis = System.currentTimeMillis();
            return claims.getExpiration().getTime() >= currentTimeMillis;
        } catch (ExpiredJwtException ex) {
            throw new IllegalArgumentException("Token has expired.");
        } catch (MalformedJwtException ex) {
            throw new IllegalArgumentException("Invalid token.");
        } catch (SignatureException ex) {
            throw new IllegalArgumentException("Token validation error.");
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Token validation error: " + ex.getMessage());
        }
    }

}
