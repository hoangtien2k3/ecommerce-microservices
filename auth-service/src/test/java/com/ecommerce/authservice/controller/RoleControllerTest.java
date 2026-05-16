package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        ResponseEntity<?> response = roleController.assignRoles(1L, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Roles have been assigned to users with IDs 1", response.getBody());
        verify(roleService).assignRole(1L, "ADMIN");
    }

    @Test
    void assignRolesWhenAlreadyAssignedShouldReturnBadRequest() {
        when(roleService.assignRole(1L, "USER")).thenReturn(false);

        ResponseEntity<?> response = roleController.assignRoles(1L, "USER");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(roleService).assignRole(1L, "USER");
    }

    @Test
    void revokeRolesShouldReturnSuccessMessage() {
        when(roleService.revokeRole(1L, "USER")).thenReturn(true);

        ResponseEntity<?> response = roleController.revokeRoles(1L, "USER");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Roles have been revoked from user ID 1", response.getBody());
        verify(roleService).revokeRole(1L, "USER");
    }

    @Test
    void revokeRolesWhenNotAssignedShouldReturnBadRequest() {
        when(roleService.revokeRole(1L, "ADMIN")).thenReturn(false);

        ResponseEntity<?> response = roleController.revokeRoles(1L, "ADMIN");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(roleService).revokeRole(1L, "ADMIN");
    }

    @Test
    void getUserRolesShouldReturnRoleList() {
        List<String> expectedRoles = List.of("USER", "ADMIN");
        when(roleService.getUserRoles(1L)).thenReturn(expectedRoles);

        ResponseEntity<List<String>> response = roleController.getUserRoles(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains("USER"));
        assertTrue(response.getBody().contains("ADMIN"));
        verify(roleService).getUserRoles(1L);
    }
}
