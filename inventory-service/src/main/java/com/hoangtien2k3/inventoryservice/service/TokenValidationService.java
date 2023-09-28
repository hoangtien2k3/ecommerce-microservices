package com.hoangtien2k3.inventoryservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenValidationService {

    @Value("${jwt.secret-key}")
    private String secretKey; // Khóa bí mật đã được cấu hình trong application.properties

    public Mono<Boolean> validateToken(String token) {
        try {
            // Parse và kiểm tra token bằng khóa bí mật
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token);

            // Kiểm tra xem token có hợp lệ không
            return Mono.just(true);
        } catch (Exception e) {
            // Xác thực thất bại
            return Mono.just(false);
        }
    }
}