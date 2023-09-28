package com.hoangtien2k3.orderservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String jwtSecret = "hoangtien2k3";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Lấy token từ header "Authorization"
        String token = extractTokenFromRequest(request);

        // Kiểm tra và xác thực token
        if (StringUtils.hasText(token) && validateToken(token)) {
            // Nếu token hợp lệ, xác thực người dùng và cho phép tiếp tục
            Authentication authentication = authenticateUser(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    // Phương thức để trích xuất token từ header "Authorization"
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Loại bỏ "Bearer " để lấy token
        }
        return null;
    }

    // Phương thức để kiểm tra tính hợp lệ của token
    private boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Phương thức để xác thực người dùng bằng token
    private Authentication authenticateUser(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        // Lấy thông tin xác thực từ token (ví dụ: ID người dùng, vai trò, v.v.)
        String userId = claims.getSubject();
        // Lấy danh sách vai trò (roles) từ token
        List<GrantedAuthority> authorities = ((List<?>) claims.get("roles")).stream()
                .map(authority -> new SimpleGrantedAuthority((String) authority))
                .collect(Collectors.toList());

        // Tạo đối tượng UserDetails để xác thực người dùng
        UserDetails userDetails = new User(userId, "", authorities);

        // Tạo đối tượng Authentication
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
}