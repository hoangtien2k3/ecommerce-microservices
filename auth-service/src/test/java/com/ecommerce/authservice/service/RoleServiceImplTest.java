package com.ecommerce.authservice.service;

import com.ecommerce.authservice.entity.Role;
import com.ecommerce.authservice.entity.RoleName;
import com.ecommerce.authservice.entity.User;
import com.ecommerce.authservice.repository.RoleRepository;
import com.ecommerce.authservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void assignRoleShouldAddRoleToUser() {
        User user = new User();
        user.setId(1L);
        user.setRoles(new HashSet<>());

        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(RoleName.ADMIN)).thenReturn(Optional.of(role));

        boolean result = roleService.assignRole(1L, "ADMIN");

        assertTrue(result);
        assertTrue(user.getRoles().contains(role));
        verify(userRepository).save(user);
    }

    @Test
    void assignRoleShouldReturnFalseWhenRoleAlreadyAssigned() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.USER);

        User user = new User();
        user.setId(1L);
        user.setRoles(new HashSet<>(Set.of(role)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(role));

        boolean result = roleService.assignRole(1L, "USER");

        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void revokeRoleShouldRemoveRoleFromUser() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.USER);

        User user = new User();
        user.setId(1L);
        user.setRoles(new HashSet<>(Set.of(role)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        boolean result = roleService.revokeRole(1L, "USER");

        assertTrue(result);
        assertTrue(user.getRoles().isEmpty());
        verify(userRepository).save(user);
    }

    @Test
    void getRolesShouldReturnUserRoleNames() {
        Role userRole = new Role();
        userRole.setName(RoleName.USER);
        Role adminRole = new Role();
        adminRole.setName(RoleName.ADMIN);

        User user = new User();
        user.setId(1L);
        user.setRoles(new HashSet<>(Set.of(userRole, adminRole)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<String> roles = roleService.getUserRoles(1L);

        assertEquals(2, roles.size());
        assertTrue(roles.contains("USER"));
        assertTrue(roles.contains("ADMIN"));
    }
}
