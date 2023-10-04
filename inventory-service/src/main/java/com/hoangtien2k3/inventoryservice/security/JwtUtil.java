package com.hoangtien2k3.inventoryservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;

public class JwtUtil {
    private static final String SECRET_KEY = "vip2023"; // Thay thế bằng khoá bí mật của bạn

    public static boolean validateToken(String token) {
        try {
            // Giải mã token và kiểm tra tính hợp lệ
            Claims claims = Jwts
                    .parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            // Kiểm tra thời gian hết hạn của token
            long currentTimeMillis = System.currentTimeMillis();
            if (claims.getExpiration().getTime() < currentTimeMillis) {
                // Token đã hết hạn
                return false;
            }

            // Kiểm tra các điều kiện khác tùy theo yêu cầu của bạn
            // Ví dụ: Kiểm tra roles hoặc thông tin khác

            // Trả về true nếu token hợp lệ
            return true;
        } catch (ExpiredJwtException e) {
            // Token đã hết hạn
            return false;
        } catch (Exception e) {
            // Xảy ra lỗi khi xác thực token
            e.printStackTrace();
            return false;
        }
    }
}
