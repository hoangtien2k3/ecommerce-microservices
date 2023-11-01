package com.hoangtien2k3.orderservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class Authorities {
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


    public static boolean checkPermission(String token, String requiredRole) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret) // Thêm khóa bí mật khi giải mã token
                    .parseClaimsJws(token)
                    .getBody();

            List<String> authorities = claims.get("authorities", List.class);
            if (authorities != null && authorities.contains(requiredRole)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    public Boolean extractRolesFromToken(String token, String requiredRole) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            List<Map<String, String>> authorities = claims.get("authorities", List.class);
            List<String> roles = extractRolesFromClaims(authorities);

            return roles.contains(requiredRole);
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> extractRolesFromClaims(List<Map<String, String>> authorities) {
        return authorities.stream()
                .map(authority -> authority.get("authority"))
                .collect(Collectors.toList());
    }


}
