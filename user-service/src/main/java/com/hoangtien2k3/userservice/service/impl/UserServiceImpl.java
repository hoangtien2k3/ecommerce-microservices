package com.hoangtien2k3.userservice.service.impl;

import com.hoangtien2k3.userservice.exception.wrapper.*;
import com.hoangtien2k3.userservice.model.dto.request.ChangePasswordRequest;
import com.hoangtien2k3.userservice.model.dto.request.EmailDetails;
import com.hoangtien2k3.userservice.model.dto.request.SignInForm;
import com.hoangtien2k3.userservice.model.dto.request.SignUpForm;
import com.hoangtien2k3.userservice.model.dto.response.InformationMessage;
import com.hoangtien2k3.userservice.model.dto.response.JwtResponseMessage;
import com.hoangtien2k3.userservice.model.entity.RoleName;
import com.hoangtien2k3.userservice.model.entity.User;
import com.hoangtien2k3.userservice.repository.UserRepository;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.security.userprinciple.UserDetailService;
import com.hoangtien2k3.userservice.security.userprinciple.UserPrinciple;
import com.hoangtien2k3.userservice.service.EmailService;
import com.hoangtien2k3.userservice.service.RoleService;
import com.hoangtien2k3.userservice.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserDetailService userDetailsService;
    private final ModelMapper modelMapper;
    private final RoleService roleService;
    private final EmailService emailService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${refresh.token.url}")
    private String refreshTokenUrl;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, UserDetailService userDetailsService, ModelMapper modelMapper, RoleService roleService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.modelMapper = modelMapper;
        this.roleService = roleService;
        this.emailService = emailService;
    }

    @Override
    public Mono<User> register(SignUpForm signUpForm) {
        return Mono.defer(() -> {
            if (existsByUsername(signUpForm.getUsername())) {
                return Mono.error(new EmailOrUsernameNotFoundException("The username " + signUpForm.getUsername() + " is existed, please try again."));
            }
            if (existsByEmail(signUpForm.getEmail())) {
                return Mono.error(new EmailOrUsernameNotFoundException("The email " + signUpForm.getEmail() + " is existed, please try again."));
            }
            if (existsByPhoneNumber(signUpForm.getPhone())) {
                return Mono.error(new PhoneNumberNotFoundException("The phone number " + signUpForm.getPhone() + " is existed, please try again."));
            }

            User user = modelMapper.map(signUpForm, User.class);
            user.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
            user.setRoles(signUpForm.getRoles()
                    .stream()
                    .map(role -> roleService.findByName(mapToRoleName(role))
                            .orElseThrow(() -> new RuntimeException("Role not found in the database.")))
                    .collect(Collectors.toSet()));

            userRepository.save(user);
            return Mono.just(user);
        });
    }

    private RoleName mapToRoleName(String roleName) {
        return switch (roleName) {
            case "ADMIN", "admin", "Admin" -> RoleName.ADMIN;
            case "PM", "pm", "Pm" -> RoleName.PM;
            case "USER", "user", "User" -> RoleName.USER;
            default -> null;
        };
    }

    @Override
    public Mono<JwtResponseMessage> login(SignInForm signInForm) {
        return Mono.fromCallable(() -> {
                    String usernameOrEmail = signInForm.getUsername();
                    boolean isEmail = usernameOrEmail.contains("@gmail.com");

                    UserDetails userDetails;
                    if (isEmail) {
                        userDetails = userDetailsService.loadUserByEmail(usernameOrEmail);
                    } else {
                        userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);
                    }

                    // check username
                    if (userDetails == null) {
                        throw new UserNotFoundException("User not found");
                    }

                    // Check password
                    if (!passwordEncoder.matches(signInForm.getPassword(), userDetails.getPassword())) {
                        throw new PasswordNotFoundException("Incorrect password");
                    }

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            signInForm.getPassword(),
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    String accessToken = jwtProvider.createToken(authentication);
                    String refreshToken = jwtProvider.createRefreshToken(authentication);

                    UserPrinciple userPrinciple = (UserPrinciple) userDetails;

                    JwtResponseMessage jwtResponseMessage = JwtResponseMessage.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .information(InformationMessage.builder()
                                    .id(userPrinciple.id())
                                    .fullname(userPrinciple.fullname())
                                    .username(userPrinciple.username())
                                    .email(userPrinciple.email())
                                    .phone(userPrinciple.phone())
                                    .gender(userPrinciple.gender())
                                    .avatar(userPrinciple.avatar())
                                    .roles(userPrinciple.roles())
                                    .build())
                            .build();

                    return Mono.just(jwtResponseMessage);
                })
                .flatMap(Mono::just)
                .onErrorResume(Mono::error).block();
    }

    @Override
    public Mono<Void> logout() {
        return Mono.defer(() -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            SecurityContextHolder.getContext().setAuthentication(null);

            String currentToken = getCurrentToken();

            if (authentication != null && authentication.isAuthenticated()) {
                // Invalidate the current token by reducing its expiration time
                String updatedToken = jwtProvider.reduceTokenExpiration(currentToken);
            }

            SecurityContextHolder.clearContext();

            return Mono.empty();
        });
    }

    private String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object credentials = authentication.getCredentials();

            if (credentials instanceof String) {
                return (String) credentials;
            }
        }

        return null;
    }

    @Transactional
    @Override
    public Mono<User> update(Long id, SignUpForm signUpForm) {
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found userId: " + id + " for update"));

            modelMapper.map(signUpForm, existingUser);
            existingUser.setPassword(passwordEncoder.encode(signUpForm.getPassword()));

            return Mono.just(userRepository.save(existingUser));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Transactional
    @Override
    public Mono<String> changePassword(ChangePasswordRequest request) {
        try {
            UserDetails userDetails = getCurrentUserDetails();
            String username = userDetails.getUsername();

            User existingUser = findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found with username " + username));

            if (passwordEncoder.matches(request.getOldPassword(), userDetails.getPassword())) {
                validateNewPassword(request.getNewPassword(), request.getConfirmPassword());
                existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));

                EmailDetails emailDetails = EmailDetails.builder()
                        .recipient("hoangtien2k3dev@gmail.com")
                        .msgBody(textSendEmailChangePasswordSuccessfully(username))
                        .subject("Password Change Successful: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .attachment("Please be careful, don't let this information leak")
                        .build();
                String message = emailService.sendMail(emailDetails);
                return Mono.just("Password changed successfully " + message);
            } else {
                return Mono.error(new PasswordNotFoundException("Incorrect password"));
            }
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public String textSendEmailChangePasswordSuccessfully(String username) {
        return "Hey " + username + "!\n\n" +
                "This is a confirmation that your password has been successfully changed.\n\n" +
                " If you did not initiate this change, please contact our support team immediately.\n\n" +
                "If you have any questions or concerns, feel free to reach out to us.\n\n" +
                "Best regards:\n\n" +
                "Contact: hoangtien2k3qx1@gmail.com\n" +
                "Fanpage: https://hoangtien2k3qx1.github.io/";
    }

    private UserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        } else {
            throw new UserNotAuthenticatedException("User not authenticated.");
        }
    }

    private void validateNewPassword(String newPassword, String confirmPassword) {
        if (!Objects.equals(newPassword, confirmPassword)) {
            throw new PasswordNotFoundException("Confirm password is incorrect");
        }
    }

    @Transactional
    @Override
    public String delete(Long id) {
        userRepository.findById(id)
                .ifPresentOrElse(
                        user -> {
                            try {
                                userRepository.delete(user);
                            } catch (DataAccessException e) {
                                throw new RuntimeException("Error deleting user with userId: " + id, e);
                            }
                        },
                        () -> {
                            throw new UserNotFoundException("User not found for userId: " + id);
                        }
                );
        return "User with id " + id + " deleted successfully.";
    }

    public Mono<String> refreshToken(String refreshToken) {
        return webClientBuilder.build()
                .post()
                .uri(refreshTokenUrl)
                .header("Refresh-Token", refreshToken)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new IllegalArgumentException("Refresh token không hợp lệ")))
                .bodyToMono(JwtResponseMessage.class)
                .map(JwtResponseMessage::getAccessToken); // Sử dụng getAccessToken để lấy token từ JwtResponse
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.of(userRepository.findById(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with userId: " + userId));
    }

    @Override
    public Optional<User> findByUsername(String userName) {
        return Optional.ofNullable(userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found with userName: " + userName)));
    }

    @Override
    public Optional<List<User>> findAllUser() {
        List<User> users = userRepository.findAll();
        return Optional.ofNullable(Optional.ofNullable(users)
                .orElseThrow(() -> new UserNotFoundException("Not found any user.")));
    }

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

    public boolean existsByPhoneNumber(String phone) {
        return userRepository.existsByPhoneNumber(phone);
    }

}
