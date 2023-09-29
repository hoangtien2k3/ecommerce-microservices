package com.hoangtien2k3.userservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenValidationService {

    @Value("${jwt.secret}")
    private String secretKey; // Khóa bí mật đã được cấu hình trong application.properties

    public boolean validateToken(String token) {
        try {
            // Parse và kiểm tra token bằng khóa bí mật
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token);

            // Kiểm tra xem token có hợp lệ không bằng cách kiểm tra thời gian hết hạn
            Date expirationDate = claims.getBody().getExpiration();
            Date currentDate = new Date();

            return !expirationDate.before(currentDate);
        } catch (Exception e) {
            // Xác thực thất bại, token không hợp lệ
            return false;
        }
    }
}