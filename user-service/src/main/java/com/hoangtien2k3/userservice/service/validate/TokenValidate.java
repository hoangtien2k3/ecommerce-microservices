package com.hoangtien2k3.userservice.service.validate;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenValidate {

    private static String secretKey;

    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKey) {
        secretKey = secretKey;
    }

    public static boolean validateToken(String token) {
        try {
            if (secretKey == null || secretKey.isEmpty()) {
                throw new IllegalArgumentException("Not found secret key in structure");
            }

            // Parse và kiểm tra token bằng khóa bí mật
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token);

            // Kiểm tra xem token có hợp lệ không bằng cách kiểm tra thời gian hết hạn
            Date expirationDate = claims.getBody().getExpiration();
            Date currentDate = new Date();

            if (expirationDate.before(currentDate)) {
                throw new IllegalArgumentException("Token has expired.");
            }

            return true;
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
