package com.hoangtien2k3.inventoryservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtValidate {

    private static String SECRET_KEY;

    @Value("${jwt.secret-key}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    public static boolean validateToken(String token) {

        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy SECRET_KEY.");
        }

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


//    public static boolean validateToken(String token) {
//        try {
//            if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
//                throw new IllegalArgumentException("Not found secret key in structure");
//            }
//
//            // Parse và kiểm tra token bằng khóa bí mật
//            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//            Jws<Claims> claims = Jwts.parser()
//                    .setSigningKey(key)
//                    .parseClaimsJws(token);
//
//            // Kiểm tra xem token có hợp lệ không bằng cách kiểm tra thời gian hết hạn
//            Date expirationDate = claims.getBody().getExpiration();
//            Date currentDate = new Date();
//
//            if (expirationDate.before(currentDate)) {
//                throw new IllegalArgumentException("Token has expired.");
//            }
//
//            return true;
//        } catch (ExpiredJwtException ex) {
//            throw new IllegalArgumentException("Token has expired.");
//        } catch (MalformedJwtException ex) {
//            throw new IllegalArgumentException("Invalid token.");
//        } catch (SignatureException ex) {
//            throw new IllegalArgumentException("Token validation error.");
//        } catch (IllegalArgumentException ex) {
//            throw new IllegalArgumentException("Token validation error: " + ex.getMessage());
//        }
//    }

}

