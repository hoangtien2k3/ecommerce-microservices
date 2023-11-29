package com.hoangtien2k3.userservice.controller;

import com.hoangtien2k3.userservice.model.dto.request.SignInForm;
import com.hoangtien2k3.userservice.model.entity.User;
import com.hoangtien2k3.userservice.http.HeaderGenerator;
import com.hoangtien2k3.userservice.repository.IUserRepository;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.service.impl.UserServiceImpl;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/information")
public class InformationUserController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final IUserRepository userRepository;
    private final HeaderGenerator headerGenerator;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;

    @Autowired
    public InformationUserController(IUserRepository userRepository, HeaderGenerator headerGenerator, JwtProvider jwtProvider, AuthenticationManager authenticationManager, UserServiceImpl userService) {
        this.userRepository = userRepository;
        this.headerGenerator = headerGenerator;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping(value = "/user/{username}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<?> getUserByUsername(@PathVariable("username") String username) {
        User user = userRepository.getUserByUsername(username);
        return (user != null)
                ? new ResponseEntity<>(user, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK)
                : new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    @PreAuthorize(value = "hasRole('USER')")
    @GetMapping(value = "/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        if (userService.findById(id).isPresent()) {
            User user = userService.findById(id).get();
            return new ResponseEntity<>(user,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/all")
    public ResponseEntity<?> getAllUsers() {
        List<User> listUsers = userService.getAllUsers()
                .orElseThrow(() -> new UsernameNotFoundException("Not Found List User"));
        return new ResponseEntity<>(listUsers,
                headerGenerator.getHeadersForSuccessGetMethod(),
                HttpStatus.OK);
    }

    @GetMapping("/user/page")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<User> users = userService.getAllUsers(page, size);
        return new ResponseEntity<>(users, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
    }

    @GetMapping("/generate/token")
    public ResponseEntity<String> getToken(@RequestBody SignInForm signInForm) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInForm.getUsernameOrEmail(), signInForm.getPassword())
        );

        String token = jwtProvider.createToken(authentication); // Đoạn mã ở đây để lấy token từ hệ thống xác thực (nếu cần)
        return ResponseEntity.ok(token); // Trả về token trong phản hồi
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
