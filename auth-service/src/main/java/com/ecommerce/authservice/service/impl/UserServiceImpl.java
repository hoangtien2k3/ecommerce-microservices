package com.ecommerce.authservice.service.impl;

import com.ecommerce.authservice.model.dto.request.*;
import com.ecommerce.authservice.model.entity.RoleName;
import com.ecommerce.authservice.model.entity.User;
import com.ecommerce.authservice.repository.UserRepository;
import com.ecommerce.authservice.service.RoleService;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.keycloak.KeycloakAuthClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final RoleService roleService;
    private final KeycloakAuthClient keycloakAuthClient;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ModelMapper modelMapper,
            RoleService roleService,
            KeycloakAuthClient keycloakAuthClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.roleService = roleService;
        this.keycloakAuthClient = keycloakAuthClient;
    }

    @Override
    @Transactional
    public User register(SignUp signUp) {
        if (existsByUsername(signUp.getUsername())) {
            throw BusinessException.conflict("The username " + signUp.getUsername() + " is existed, please try again.");
        }
        if (existsByEmail(signUp.getEmail())) {
            throw BusinessException.conflict("The email " + signUp.getEmail() + " is existed, please try again.");
        }
        if (existsByPhoneNumber(signUp.getPhone())) {
            throw BusinessException.conflict("The phone number " + signUp.getPhone() + " is existed, please try again.");
        }

        List<String> requestedRoles = normalizeRequestedRoles(signUp.getRoles());
        String keycloakUserId = keycloakAuthClient.createUser(
            signUp.getUsername(),
            signUp.getEmail(),
            signUp.getFullname(),
            signUp.getPassword(),
            requestedRoles
        );

        try {
            User user = modelMapper.map(signUp, User.class);
            user.setPassword(passwordEncoder.encode(signUp.getPassword()));
            user.setKeycloakUserId(keycloakUserId);
            user.setRoles(requestedRoles
                .stream()
                .map(role -> roleService.findByName(mapToRoleName(role))
                    .orElseThrow(() -> BusinessException.notFound("Role not found in the database: " + role)))
                .collect(Collectors.toSet()));

            return userRepository.save(user);
        } catch (RuntimeException ex) {
            keycloakAuthClient.deleteUser(keycloakUserId);
            throw ex;
        }
    }

    private RoleName mapToRoleName(String roleName) {
        return switch (roleName) {
            case "ADMIN", "admin", "Admin" -> RoleName.ADMIN;
            case "PM", "pm", "Pm" -> RoleName.PM;
            case "USER", "user", "User" -> RoleName.USER;
            default -> throw BusinessException.badRequest("Unsupported role: " + roleName);
        };
    }

    @Transactional
    @Override
    public User update(Long id, SignUp updateDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("User not found userId: " + id + " for update"));

        modelMapper.map(updateDTO, existingUser);
        existingUser.setPassword(passwordEncoder.encode(updateDTO.getPassword()));

        return userRepository.save(existingUser);
    }

    @Transactional
    @Override
    public String changePassword(ChangePasswordRequest request) {
        String username = getCurrentUsername();

        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found with username " + username));

        if (passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())) {
            if (validateNewPassword(request.getNewPassword(), request.getConfirmPassword())) {
                existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(existingUser);
                return "Password changed successfully";
            }

            return "Password changed failed.";
        } else {
            throw BusinessException.badRequest("Incorrect password");
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw BusinessException.unauthorized("User not authenticated.");
        }
        return authentication.getName();
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
                    throw BusinessException.notFound("User not found for userId: " + id);
                }
            );
        return "User with id " + id + " deleted successfully.";
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> BusinessException.notFound("User not found with userId: " + userId));
    }

    @Override
    public User findByUsername(String userName) {
        return userRepository.findByUsername(userName)
            .orElseThrow(() -> BusinessException.notFound("User not found with userName: " + userName));
    }

    @Override
    public Page<UserDto> findAllUsers(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<User> usersPage = userRepository.findAll(pageRequest);
        return usersPage.map(user -> modelMapper.map(user, UserDto.class));
    }

    private List<String> normalizeRequestedRoles(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return List.of("USER");
        }
        return roles.stream().map(String::trim).filter(value -> !value.isBlank()).toList();
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
