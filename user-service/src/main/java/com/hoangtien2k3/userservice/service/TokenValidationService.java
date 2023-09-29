package com.hoangtien2k3.userservice.service;

import io.jsonwebtoken.*;
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
            if (secretKey == null || secretKey.isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy khóa bí mật trong cấu hình.");
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
                throw new IllegalArgumentException("Token đã hết hạn.");
            }

            return true;
        } catch (ExpiredJwtException ex) {
            throw new IllegalArgumentException("Token đã hết hạn.");
        } catch (MalformedJwtException ex) {
            throw new IllegalArgumentException("Token không hợp lệ.");
        } catch (SignatureException ex) {
            throw new IllegalArgumentException("Lỗi xác thực token.");
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Lỗi xác thực token: " + ex.getMessage());
        }
    }
}