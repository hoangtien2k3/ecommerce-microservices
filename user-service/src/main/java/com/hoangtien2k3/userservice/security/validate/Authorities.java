package com.hoangtien2k3.userservice.security.validate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class Authorities {

//    @Value("${jwt.secret}")
    private static String jwtSecret = "vip2023";

    public static List<String> extractAuthoritiesFromToken(String token) {
        try {
            // Giả định rằng bạn sử dụng thư viện JWT (JSON Web Token) để giải mã token
            Jws<Claims> claims = Jwts
                    .parser()
                    .setSigningKey(jwtSecret) // Thêm khóa bí mật khi giải mã token
                    .parseClaimsJws(token);

            // Trích xuất phần "authorities" từ claims
            Claims body = claims.getBody();
            List<String> authorities = new ArrayList<>();
            List<Object> roles = body.get("authorities", List.class);

            // Trích xuất danh sách quyền từ mảng "authority"
            for (Object role : roles) {
                if (role instanceof String) {
                    authorities.add((String) role);
                }
            }

            // Trả về danh sách quyền
            return authorities;
        } catch (Exception e) {
            return new ArrayList<>(); // Trả về danh sách trống nếu xảy ra lỗi
        }
    }
}
