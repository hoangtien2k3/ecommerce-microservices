package com.hoangtien2k3.userservice.security.jwt;

import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.security.userprinciple.UserPrinciple;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    // ghi log
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")  // Khóa bí mật, token sẽ được mã hóa với khóa này
    private String jwtSecret;
    @Value("${jwt.expiration}") // Thời gian hiệu  lực của token
    private int jwtExpiration;
    @Value("${jwt.refreshExpiration}")
    private int jwtRefreshExpiration;


    // generate token
    public String createToken(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrinciple.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpiration * 1000L))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrinciple.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtRefreshExpiration * 1000L)) // jwtRefreshExpiration là thời gian hiệu lực của refresh token
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // check xem token có hợp lệ hay không
    public Boolean validateToken(String token) {
        try {

            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);
            return true;

        } catch (SignatureException e) {
            logger.error("Invalid JWT signature -> Message: ", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid format Token -> Message: ", e);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT Token -> Message: ", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT Token -> Message: ", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty -> Message: ", e);
        }

        return false;
    }

    // lấy ra Username từ token
    public String getUserNameFromToken(String token) {
        String userName = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return userName;
    }

}
