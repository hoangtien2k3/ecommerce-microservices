package com.hoangtien2k3.userservice.service.impl;

import com.google.gson.Gson;
import com.hoangtien2k3.userservice.constant.KafkaConstant;
import com.hoangtien2k3.userservice.event.EventProducer;
import com.hoangtien2k3.userservice.exception.wrapper.*;
import com.hoangtien2k3.userservice.model.dto.request.*;
import com.hoangtien2k3.userservice.model.dto.response.InformationMessage;
import com.hoangtien2k3.userservice.model.dto.response.JwtResponseMessage;
import com.hoangtien2k3.userservice.model.entity.RoleName;
import com.hoangtien2k3.userservice.model.entity.User;
import com.hoangtien2k3.userservice.repository.UserRepository;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.security.userprinciple.UserDetailService;
import com.hoangtien2k3.userservice.security.userprinciple.UserPrinciple;
import com.hoangtien2k3.userservice.service.RoleService;
import com.hoangtien2k3.userservice.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

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

    Gson gson = new Gson(); // google.code.gson
    @Autowired
    EventProducer eventProducer;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${refresh.token.url}")
    private String refreshTokenUrl;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtProvider jwtProvider,
                           UserDetailService userDetailsService,
                           ModelMapper modelMapper,
                           RoleService roleService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.modelMapper = modelMapper;
        this.roleService = roleService;
    }

    @Override
    public Mono<User> register(SignUp signUp) {
        return Mono.defer(() -> {
            if (existsByUsername(signUp.getUsername())) {
                return Mono.error(new EmailOrUsernameNotFoundException("The username " + signUp.getUsername() + " is existed, please try again."));
            }
            if (existsByEmail(signUp.getEmail())) {
                return Mono.error(new EmailOrUsernameNotFoundException("The email " + signUp.getEmail() + " is existed, please try again."));
            }
            if (existsByPhoneNumber(signUp.getPhone())) {
                return Mono.error(new PhoneNumberNotFoundException("The phone number " + signUp.getPhone() + " is existed, please try again."));
            }

            User user = modelMapper.map(signUp, User.class);
            user.setPassword(passwordEncoder.encode(signUp.getPassword()));
            user.setRoles(signUp.getRoles()
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
    public Mono<JwtResponseMessage> login(Login signInForm) {
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

            return JwtResponseMessage.builder()
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
        }).onErrorResume(Mono::error);
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
    public Mono<User> update(Long id, SignUp updateDTO) {
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found userId: " + id + " for update"));

            modelMapper.map(updateDTO, existingUser);
            existingUser.setPassword(passwordEncoder.encode(updateDTO.getPassword()));

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
                if (validateNewPassword(request.getNewPassword(), request.getConfirmPassword())) {
                    existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    userRepository.save(existingUser);

                    // send email through kafka client
                    EmailDetails emailDetails = emailDetailsConfig(username);

                    return eventProducer.send(KafkaConstant.PROFILE_ONBOARDING_TOPIC, gson.toJson(emailDetails))
                            .thenReturn("Password changed successfully")
                            .publishOn(Schedulers.boundedElastic());
                }

                return Mono.just("Password changed failed.");
            } else {
                return Mono.error(new PasswordNotFoundException("Incorrect password"));
            }
        } catch (Exception e) {
            return Mono.error(new UserNotAuthenticatedException("Transaction silently rolled back"));
        }
    }

    private EmailDetails emailDetailsConfig(String username) {
        return EmailDetails.builder()
                .recipient("hoangtien2k3dev@gmail.com")
                .msgBody(textSendEmailChangePasswordSuccessfully(username))
                .subject("Password Change Successful: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .attachment("Please be careful, don't let this information leak")
                .build();
    }

    public String textSendEmailChangePasswordSuccessfully(String username) {
        return "Hey " + username + "!\n\n" +
                "This is a confirmation that your password has been successfully changed.\n" +
                " If you did not initiate this change, please contact our support team immediately.\n" +
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

    private boolean validateNewPassword(String newPassword, String confirmPassword) {
        return Objects.equals(newPassword, confirmPassword);
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
    public Page<UserDto> findAllUsers(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<User> usersPage = userRepository.findAll(pageRequest);

        return usersPage.map(user -> modelMapper.map(user, UserDto.class));
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
