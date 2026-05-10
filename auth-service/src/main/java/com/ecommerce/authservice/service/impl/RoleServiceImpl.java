package com.ecommerce.authservice.service.impl;

import com.ecommerce.authservice.model.entity.Role;
import com.ecommerce.authservice.model.entity.RoleName;
import com.ecommerce.authservice.model.entity.User;
import com.ecommerce.authservice.repository.RoleRepository;
import com.ecommerce.authservice.repository.UserRepository;
import com.ecommerce.authservice.service.RoleService;
import com.ecommerce.commonlib.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.*;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        return Optional.ofNullable(roleRepository.findByName(name)
                .orElseThrow(() -> BusinessException.notFound("auth.role.not.found.with.name", name)));
    }

    @Transactional
    @Override
    public boolean assignRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("auth.user.not.found.with.id", userId));

        Role role = roleRepository.findByName(mapToRoleName(roleName))
                .orElseThrow(() -> BusinessException.notFound("auth.role.not.found.in.system", roleName));

        if (user.getRoles().contains(role))
            return false;

        user.getRoles().add(role);
        userRepository.save(user);
        return true;
    }

    @Transactional
    @Override
    public boolean revokeRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("auth.user.not.found"));

        if (user.getRoles().removeIf(role -> role.name().equals(mapToRoleName(roleName)))) {
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getUserRoles(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("auth.user.not.found"));

        List<String> roleNames = new ArrayList<>();
        user.getRoles().forEach(userRole -> roleNames.add(userRole.name().toString()));
        return roleNames;
    }

    private RoleName mapToRoleName(String roleName) {
        return switch (roleName) {
            case "ADMIN", "admin", "Admin" -> RoleName.ADMIN;
            case "PM", "pm", "Pm" -> RoleName.PM;
            case "USER", "user", "User" -> RoleName.USER;
            default -> throw BusinessException.badRequest("auth.unsupported.role", roleName);
        };
    }

}
