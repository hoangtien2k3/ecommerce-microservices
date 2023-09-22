package com.hoangtien2k3.userservice.controller;

import com.hoangtien2k3.userservice.dto.request.SignUpFrom;
import com.hoangtien2k3.userservice.dto.response.JwtResponse;
import com.hoangtien2k3.userservice.dto.response.ResponseMessge;
import com.hoangtien2k3.userservice.entity.Role;
import com.hoangtien2k3.userservice.entity.RoleName;
import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.security.userprinciple.UserPrinciple;
import com.hoangtien2k3.userservice.service.impl.RoleServiceImpl;
import com.hoangtien2k3.userservice.service.impl.UserServiceImpl;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;


    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody SignUpFrom signUpFrom) {
        if (userService.existsByUsername(signUpFrom.getUsername())) {
            return new ResponseEntity<>(new ResponseMessge("The username existed, please try again."), HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByEmail(signUpFrom.getEmail())) {
            return new ResponseEntity<>(new ResponseMessge("The email existed, please try again."), HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .name(signUpFrom.getName())
                .username(signUpFrom.getUsername())
                .email(signUpFrom.getEmail())
                .password(passwordEncoder.encode(signUpFrom.getPassword()))
                .build();

        Set<String> strRoles = signUpFrom.getRoles();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch (role) {
                case "admin": {
                    Role adminRole = roleService
                            .findByName(RoleName.ADMIN)
                            .orElseThrow(() -> new RuntimeException("Role not found."));
                    roles.add(adminRole);
                    break;
                }
                case "pm": {
                    Role pmRole = roleService
                            .findByName(RoleName.PM)
                            .orElseThrow(() -> new RuntimeException("Role not found."));
                    roles.add(pmRole);
                    break;
                }
                default: {
                    Role userRole = roleService
                            .findByName(RoleName.USER)
                            .orElseThrow(() -> new RuntimeException("Role not found."));
                    roles.add(userRole);
                    break;
                }
            }
        });

        user.setRoles(roles);
        userService.save(user);

        return new ResponseEntity<>(new ResponseMessge("Create User Successfully."), HttpStatus.OK);
    }


    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody SignUpFrom signUpFrom) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signUpFrom.getUsername(), signUpFrom.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // generate token by authentication
        String token = jwtProvider.createToken(authentication);
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(token, userPrinciple.getId() , userPrinciple.getName(), userPrinciple.getAuthorities()));
    }

}
