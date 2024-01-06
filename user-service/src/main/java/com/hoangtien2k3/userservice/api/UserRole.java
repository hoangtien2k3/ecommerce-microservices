package com.hoangtien2k3.userservice.api;

import com.hoangtien2k3.userservice.http.HeaderGenerator;
import com.hoangtien2k3.userservice.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/role")
@Api(value = "User Role API", description = "Operations related to user roles")
public class UserRole {

    private final RoleService roleService;
    private final HeaderGenerator headerGenerator;

    @Autowired
    public UserRole(RoleService roleService, HeaderGenerator headerGenerator) {
        this.roleService = roleService;
        this.headerGenerator = headerGenerator;
    }

    @ApiOperation(value = "Assign roles to user", notes = "Assign roles to a user with the specified ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Roles assigned successfully", response = String.class),
            @ApiResponse(code = 400, message = "Bad Request", response = String.class)
    })
    @PostMapping("/{id}/assign-roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> assignRoles(@PathVariable Long id, @RequestBody String roleNames) {
        boolean success = roleService.assignRole(id, roleNames);
        if (success) {
            return new ResponseEntity<>("Roles have been assigned to users with IDs " + id,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>("Has full rights for the User" + id,
                headerGenerator.getHeadersForError(),
                HttpStatus.OK);
    }

    @ApiOperation(value = "Revoke roles from user", notes = "Revoke roles from a user with the specified ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Roles revoked successfully", response = String.class),
            @ApiResponse(code = 400, message = "Bad Request", response = String.class)
    })
    @PostMapping("/{id}/revoke-roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> revokeRoles(@PathVariable Long id, @RequestBody String roleNames) {
        boolean success = roleService.revokeRole(id, roleNames);
        if (success) {
            return new ResponseEntity<>("Roles have been assigned to users with IDs " + id,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>("Has full rights for the User" + id,
                headerGenerator.getHeadersForError(),
                HttpStatus.OK);
    }

    @ApiOperation(value = "Get user roles", notes = "Retrieve roles assigned to a user with the specified ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User roles retrieved successfully", response = List.class),
            @ApiResponse(code = 404, message = "User not found", response = ResponseEntity.class)
    })
    @GetMapping("/{id}/user-roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable Long id) {
        List<String> userRoles = roleService.getUserRoles(id);
        return new ResponseEntity<>(userRoles,
                headerGenerator.getHeadersForSuccessGetMethod(),
                HttpStatus.OK);
    }

}
