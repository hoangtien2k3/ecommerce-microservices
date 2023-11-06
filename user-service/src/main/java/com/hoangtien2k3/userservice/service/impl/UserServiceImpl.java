package com.hoangtien2k3.userservice.service.impl;

import com.hoangtien2k3.userservice.dto.model.TokenManager;
import com.hoangtien2k3.userservice.dto.request.SignInForm;
import com.hoangtien2k3.userservice.dto.request.SignUpForm;
import com.hoangtien2k3.userservice.dto.response.JwtResponse;
import com.hoangtien2k3.userservice.entity.Role;
import com.hoangtien2k3.userservice.entity.RoleName;
import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.repository.IRoleRepository;
import com.hoangtien2k3.userservice.repository.IUserRepository;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.security.userprinciple.UserDetailService;
import com.hoangtien2k3.userservice.security.userprinciple.UserPrinciple;
import com.hoangtien2k3.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenManager tokenManager;
    private final UserDetailService userDetailsService;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Value("${refresh.token.url}") // Đường dẫn endpoint để refresh token
    private String refreshTokenUrl;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository, IRoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtProvider jwtProvider, TokenManager tokenManager, UserDetailService userDetailsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;
    }

    // register user
    @Override
    public Mono<User> registerUser(SignUpForm signUpForm) {
        return Mono.defer(() -> {
            if (existsByUsername(signUpForm.getUsername())) {
                return Mono.error(new RuntimeException("The username " + signUpForm.getUsername() + " is existed, please try again."));
            }
            if (existsByEmail(signUpForm.getEmail())) {
                return Mono.error(new RuntimeException("The email " + signUpForm.getEmail() + " is existed, please try again."));
            }

            Set<Role> roles = new HashSet<>();

            signUpForm.getRoles().forEach(role -> {
                    RoleName roleName = null;
                    switch (role) {
                        case "admin": case "ADMIN":
                            roleName = RoleName.ADMIN;
                            break;
                        case "PM": case "pm":
                            roleName = RoleName.PM;
                            break;
                        case "USER": case "user":
                            roleName = RoleName.USER;
                            break;
                    }

                    Role userRole = roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found."));
                    roles.add(userRole);

            });

            User user = User.builder()
                    .name(signUpForm.getName())
                    .username(signUpForm.getUsername())
                    .email(signUpForm.getEmail())
                    .password(passwordEncoder.encode(signUpForm.getPassword()))
                    .avatar("https://www.facebook.com/photo/?fbid=723931439407032&set=pob.100053705482952")
                    .roles(roles)
                    .build();

            userRepository.save(user);

            return Mono.just(user);
        });
    }


    // login email or username
    @Override
    public Mono<JwtResponse> login(SignInForm signInForm) {
        return Mono.defer(() -> {
            String usernameOrEmail = signInForm.getUsername();
            boolean isEmail = usernameOrEmail.contains("@");

            UserDetails userDetails;
            if (isEmail) {
                userDetails = userDetailsService.loadUserByEmail(usernameOrEmail);
            } else {
                userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, signInForm.getPassword(), userDetails.getAuthorities());

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            // Generate token and refresh token using JwtProvider
            String accessToken = jwtProvider.createToken(authentication);
            String refreshToken = jwtProvider.createRefreshToken(authentication);

            UserPrinciple userPrinciple = (UserPrinciple) userDetails;

            // Store the token and refresh token using TokenManager
            tokenManager.storeToken(userPrinciple.getUsername(), accessToken);
            tokenManager.storeRefreshToken(userPrinciple.getUsername(), refreshToken);

            JwtResponse jwtResponse = new JwtResponse(
                    accessToken,
                    refreshToken,
                    userPrinciple.getId(),
                    userPrinciple.getName(),
                    userPrinciple.getAuthorities()
            );

            return Mono.just(jwtResponse);
        });
    }

    public Mono<String> refreshToken(String refreshToken) {
        return webClientBuilder.build()
                .post()
                .uri(refreshTokenUrl)
                .header("Refresh-Token", refreshToken)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new IllegalArgumentException("Refresh token không hợp lệ")))
                .bodyToMono(JwtResponse.class)
                .map(JwtResponse::getAccessToken); // Sử dụng getAccessToken để lấy token từ JwtResponse
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userRepository.findById(id))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }


    // get all user in list user
    public Optional<List<User>> getAllUsers() {
        return Optional.ofNullable(userRepository.findAll());
    }


    // load user by page and size
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }


    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
