package com.ecommerce.authservice.service;

import com.ecommerce.authservice.entity.Role;
import com.ecommerce.authservice.entity.RoleName;
import com.ecommerce.authservice.entity.User;
import com.ecommerce.authservice.mapper.RoleMapper;
import com.ecommerce.authservice.repository.RoleRepository;
import com.ecommerce.authservice.repository.UserRepository;
import com.ecommerce.commonlib.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

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

        Role role = roleRepository.findByName(RoleMapper.toRoleName(roleName))
                .orElseThrow(() -> BusinessException.notFound("auth.role.not.found.in.system", roleName));

        if (user.getRoles().contains(role)) {
            return false;
        }

        user.getRoles().add(role);
        userRepository.save(user);
        return true;
    }

    @Transactional
    @Override
    public boolean revokeRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("auth.user.not.found"));

        if (user.getRoles().removeIf(role -> role.getName().equals(RoleMapper.toRoleName(roleName)))) {
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
        user.getRoles().forEach(userRole -> roleNames.add(userRole.getName().toString()));
        return roleNames;
    }
}
