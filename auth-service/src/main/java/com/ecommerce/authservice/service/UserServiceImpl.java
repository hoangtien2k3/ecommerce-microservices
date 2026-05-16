package com.ecommerce.authservice.service;

import com.ecommerce.authservice.dto.request.ChangePasswordRequest;
import com.ecommerce.authservice.dto.request.RegisterRequest;
import com.ecommerce.authservice.dto.request.UpdateUserRequest;
import com.ecommerce.authservice.dto.response.UserResponse;
import com.ecommerce.authservice.entity.RoleName;
import com.ecommerce.authservice.entity.User;
import com.ecommerce.authservice.mapper.RoleMapper;
import com.ecommerce.authservice.mapper.UserMapper;
import com.ecommerce.authservice.repository.UserRepository;
import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.keycloak.KeycloakAuthClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final KeycloakAuthClient keycloakAuthClient;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw BusinessException.conflict("auth.username.exists", request.getUsername());
        }
        if (existsByEmail(request.getEmail())) {
            throw BusinessException.conflict("auth.email.exists", request.getEmail());
        }
        if (existsByPhoneNumber(request.getPhone())) {
            throw BusinessException.conflict("auth.phone.exists", request.getPhone());
        }

        List<String> requestedRoles = normalizeRequestedRoles(request.getRoles());
        String keycloakUserId = keycloakAuthClient.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getFullName(),
                request.getPassword(),
                requestedRoles
        );

        try {
            User user = userMapper.toEntity(request);
            user.setKeycloakUserId(keycloakUserId);
            user.setRoles(requestedRoles.stream()
                    .map(RoleMapper::toRoleName)
                    .map(roleName -> roleService.findByName(roleName)
                            .orElseThrow(() -> BusinessException.notFound("auth.role.not.found.in.database", roleName)))
                    .collect(Collectors.toSet()));

            return userRepository.save(user);
        } catch (RuntimeException ex) {
            keycloakAuthClient.deleteUser(keycloakUserId);
            throw ex;
        }
    }

    @Transactional
    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("auth.user.not.found.for.update", id));

        if (request.getFullName() != null) existingUser.setFullName(request.getFullName());
        if (request.getEmail() != null) existingUser.setEmail(request.getEmail());
        if (request.getGender() != null) existingUser.setGender(request.getGender());
        if (request.getPhone() != null) existingUser.setPhone(request.getPhone());
        if (request.getAvatar() != null) existingUser.setAvatar(request.getAvatar());

        User saved = userRepository.save(existingUser);
        return userMapper.toResponse(saved);
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
                        () -> BusinessException.notFound("auth.user.not.found.for.user.id", id)
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
    public Page<UserResponse> findAllUsers(int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<User> usersPage = userRepository.findAll(pageRequest);
        return usersPage.map(userMapper::toResponse);
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
        return userRepository.existsByPhone(phone);
    }
}
