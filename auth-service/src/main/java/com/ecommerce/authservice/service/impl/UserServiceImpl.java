package com.ecommerce.authservice.service.impl;

import com.ecommerce.authservice.model.dto.request.*;
import com.ecommerce.authservice.model.entity.RoleName;
import com.ecommerce.authservice.model.entity.User;
import com.ecommerce.authservice.repository.UserRepository;
import com.ecommerce.authservice.service.RoleService;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.keycloak.KeycloakAuthClient;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleService roleService;
    private final KeycloakAuthClient keycloakAuthClient;

    @Override
    @Transactional
    public User register(SignUp signUp) {
        if (existsByUsername(signUp.getUsername())) {
            throw BusinessException.conflict("auth.username.exists", signUp.getUsername());
        }
        if (existsByEmail(signUp.getEmail())) {
            throw BusinessException.conflict("auth.email.exists", signUp.getEmail());
        }
        if (existsByPhoneNumber(signUp.getPhone())) {
            throw BusinessException.conflict("auth.phone.exists", signUp.getPhone());
        }

        List<String> requestedRoles = normalizeRequestedRoles(signUp.getRoles());
        String keycloakUserId = keycloakAuthClient.createUser(
            signUp.getUsername(),
            signUp.getEmail(),
            signUp.getFullName(),
            signUp.getPassword(),
            requestedRoles
        );

        try {
            User user = modelMapper.map(signUp, User.class);
            user.setKeycloakUserId(keycloakUserId);
            user.setRoles(requestedRoles
                .stream()
                .map(role -> roleService.findByName(mapToRoleName(role))
                    .orElseThrow(() -> BusinessException.notFound("auth.role.not.found.in.database", role)))
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
            default -> throw BusinessException.badRequest("auth.unsupported.role", roleName);
        };
    }

    @Transactional
    @Override
    public User update(Long id, SignUp updateDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("auth.user.not.found.for.update", id));

        modelMapper.map(updateDTO, existingUser);

        return userRepository.save(existingUser);
    }

    @Transactional
    @Override
    public String changePassword(ChangePasswordRequest request) {
        throw BusinessException.badRequest("auth.password.managed.by.keycloak");
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
                    throw BusinessException.notFound("auth.user.not.found.for.user.id", id);
                }
            );
        return "User with id " + id + " deleted successfully.";
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> BusinessException.notFound("auth.user.not.found.with.user.id", userId));
    }

    @Override
    public User findByUsername(String userName) {
        return userRepository.findByUsername(userName)
            .orElseThrow(() -> BusinessException.notFound("auth.user.not.found.with.username", userName));
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
