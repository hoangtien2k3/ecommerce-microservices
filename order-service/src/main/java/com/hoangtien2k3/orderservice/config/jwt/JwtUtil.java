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

            // Kiểm tra thời gian hết hạn của token
            long currentTimeMillis = System.currentTimeMillis();
            // Token đã hết hạn
            return claims.getExpiration().getTime() >= currentTimeMillis;

            // TODO: SET ROLE ...
        } catch (ExpiredJwtException e) {
            // Token đã hết hạn
            return false;
        } catch (Exception e) {
            // Xảy ra lỗi khi xác thực token
            log.error("Fail validate token");
            return false;
        }
    }
}


