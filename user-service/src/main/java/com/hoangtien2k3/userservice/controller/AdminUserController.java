package com.hoangtien2k3.userservice.controller;

import com.hoangtien2k3.userservice.dto.request.SignInForm;
import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.http.HeaderGenerator;
import com.hoangtien2k3.userservice.repository.IUserRepository;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.service.impl.UserServiceImpl;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin")
public class AdminUserController {

    private final IUserRepository userRepository;
    private final HeaderGenerator headerGenerator;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    @Value("${jwt.secret}")
    private String jwtSecret;

    public AdminUserController(IUserRepository userRepository, HeaderGenerator headerGenerator, JwtProvider jwtProvider, AuthenticationManager authenticationManager, UserServiceImpl userService, String jwtSecret) {
        this.userRepository = userRepository;
        this.headerGenerator = headerGenerator;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtSecret = jwtSecret;
    }

    // get all user profile
    @GetMapping(value = "/user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable("username") String username) {

        User user = userRepository.getUserByUsername(username);
        if (user != null) {
            return new ResponseEntity<>(user,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(null,
                headerGenerator.getHeadersForError(),
                HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/all")
    public ResponseEntity<?> getAllUsers() {

        List<User> listUsers = userService.getAllUsers()
                .orElseThrow(() -> new UsernameNotFoundException("Not Found List User"));

        return new ResponseEntity<>(listUsers,
                headerGenerator.getHeadersForError(),
                HttpStatus.OK);
    }

    @GetMapping("/generate/token")
    public ResponseEntity<String> getToken(@RequestBody SignInForm signInForm) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInForm.getUsername(), signInForm.getPassword())
        );

        // Đoạn mã ở đây để lấy token từ hệ thống xác thực (nếu cần)
         String token = jwtProvider.createToken(authentication);

        // Trả về token trong phản hồi
        return ResponseEntity.ok(token);
    }

    @GetMapping("/token")
    public String getUsernameFromToken(@RequestParam("token") String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
