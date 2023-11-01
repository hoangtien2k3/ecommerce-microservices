package com.hoangtien2k3.orderservice.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    private static String SECRET_KEY;

    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    public static boolean validateToken(String token) {
        if (token.startsWith("Bearer ")) token = token.replace("Bearer ", "");
        try {
            // Giải mã token và kiểm tra tính hợp lệ
            Claims claims = Jwts
                    .parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            long currentTimeMillis = System.currentTimeMillis(); // Kiểm tra thời gian hết hạn của token
            return claims.getExpiration().getTime() >= currentTimeMillis; // Token đã hết hạn
        } catch (ExpiredJwtException e) {
            return false; // Token đã hết hạn
        } catch (Exception e) {
            log.error("Fail validate token"); // Xảy ra lỗi khi xác thực token
            return false;
        }
    }
}


