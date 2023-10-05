package com.hoangtien2k3.userservice.service.validate;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenValidate {

    private static String secretKey;

    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKey) {
        secretKey = secretKey;
    }

    public static boolean validateToken(String token) {

        if (secretKey == null || secretKey.isEmpty()) throw new IllegalArgumentException("Not found secret key in structure");

        if (token.startsWith("Bearer ")) token = token.replace("Bearer ", "");

        try {
            // Giải mã token và kiểm tra tính hợp lệ
            Claims claims = Jwts
                    .parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            // Kiểm tra thời gian hết hạn của token
            long currentTimeMillis = System.currentTimeMillis();
            // Token đã hết hạn
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
