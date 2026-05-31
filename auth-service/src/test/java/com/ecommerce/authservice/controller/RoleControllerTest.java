package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.service.RoleService;
import com.ecommerce.commonlib.exception.BusinessException;
import com.ecommerce.commonlib.viewmodel.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    private RoleController roleController;

    @BeforeEach
    void setUp() {
        roleController = new RoleController(roleService);
    }

    @Test
    void assignRolesShouldReturnSuccessMessage() {
        when(roleService.assignRole(1L, "ADMIN")).thenReturn(true);

        ApiResponse<Void> response = roleController.assignRoles(1L, "ADMIN");

        assertTrue(response.success());
        assertEquals("Roles assigned to user 1", response.message());
        verify(roleService).assignRole(1L, "ADMIN");
    }

    @Test
    void assignRolesWhenAlreadyAssignedShouldThrow() {
        when(roleService.assignRole(1L, "USER")).thenReturn(false);

        assertThrows(BusinessException.class, () -> roleController.assignRoles(1L, "USER"));
        verify(roleService).assignRole(1L, "USER");
    }

    @Test
    void revokeRolesShouldReturnSuccessMessage() {
        when(roleService.revokeRole(1L, "USER")).thenReturn(true);

        ApiResponse<Void> response = roleController.revokeRoles(1L, "USER");

        assertTrue(response.success());
        assertEquals("Roles revoked from user 1", response.message());
        verify(roleService).revokeRole(1L, "USER");
    }

    @Test
    void revokeRolesWhenNotAssignedShouldThrow() {
        when(roleService.revokeRole(1L, "ADMIN")).thenReturn(false);

        assertThrows(BusinessException.class, () -> roleController.revokeRoles(1L, "ADMIN"));
        verify(roleService).revokeRole(1L, "ADMIN");
    }

    @Test
    void getUserRolesShouldReturnRoleList() {
        List<String> expectedRoles = List.of("USER", "ADMIN");
        when(roleService.getUserRoles(1L)).thenReturn(expectedRoles);

        ApiResponse<List<String>> response = roleController.getUserRoles(1L);

        assertTrue(response.success());
        assertEquals(2, response.data().size());
        assertTrue(response.data().contains("USER"));
        assertTrue(response.data().contains("ADMIN"));
        verify(roleService).getUserRoles(1L);
    }
}
