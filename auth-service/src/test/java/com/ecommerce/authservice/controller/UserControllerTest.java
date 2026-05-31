package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.dto.request.UpdateUserRequest;
import com.ecommerce.authservice.dto.response.UserResponse;
import com.ecommerce.authservice.entity.User;
import com.ecommerce.authservice.mapper.UserMapper;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.viewmodel.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserMapper userMapper;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper(new ModelMapper());
        userController = new UserController(userService, userMapper);
    }

    @Test
    void updateShouldReturnSuccessMessage() {
        when(userService.update(anyLong(), any(UpdateUserRequest.class)))
                .thenReturn(UserResponse.builder().build());

        ApiResponse<UserResponse> response = userController.update(1L, new UpdateUserRequest());

        assertTrue(response.success());
        assertEquals("User updated successfully", response.message());
        verify(userService).update(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    void getUserByUsernameShouldReturnUserResponse() {
        User user = new User();
        user.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(user);

        ApiResponse<UserResponse> response = userController.getUserByUsername("testuser");

        assertTrue(response.success());
        assertEquals("testuser", response.data().getUsername());
    }

    @Test
    void getUserByIdShouldReturnUserResponse() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.findById(1L)).thenReturn(user);

        ApiResponse<UserResponse> response = userController.getUserById(1L);

        assertTrue(response.success());
        assertEquals("testuser", response.data().getUsername());
    }

    @Test
    void getAllUsersShouldReturnPagedResponse() {
        UserResponse userResponse = UserResponse.builder().id(1L).username("testuser").build();
        Page<UserResponse> page = new PageImpl<>(List.of(userResponse));

        when(userService.findAllUsers(0, 10, "id", "ASC")).thenReturn(page);

        ApiResponse<Page<UserResponse>> response = userController.getAllUsers(0, 10, "id", "ASC");

        assertTrue(response.success());
        assertEquals(1, response.data().getContent().size());
    }

    @Test
    void deleteShouldReturnSuccessMessage() {
        when(userService.delete(1L)).thenReturn("User with id 1 deleted successfully.");

        ApiResponse<Void> response = userController.delete(1L);

        assertTrue(response.success());
        assertEquals("User 1 deleted successfully", response.message());
    }
}
