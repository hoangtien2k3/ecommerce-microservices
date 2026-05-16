package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.dto.request.UpdateUserRequest;
import com.ecommerce.authservice.dto.response.ResponseMessage;
import com.ecommerce.authservice.dto.response.UserResponse;
import com.ecommerce.authservice.entity.User;
import com.ecommerce.authservice.mapper.UserMapper;
import com.ecommerce.authservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

        ResponseEntity<ResponseMessage> response = userController.update(1L, new UpdateUserRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Update user successfully.", response.getBody().getMessage());
        verify(userService).update(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    void getUserByUsernameShouldReturnUserResponse() {
        User user = new User();
        user.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(user);

        ResponseEntity<UserResponse> response = userController.getUserByUsername("testuser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    void getUserByIdShouldReturnUserResponse() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.findById(1L)).thenReturn(user);

        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    void getAllUsersShouldReturnPagedResponse() {
        UserResponse userResponse = UserResponse.builder().id(1L).username("testuser").build();
        Page<UserResponse> page = new PageImpl<>(List.of(userResponse));

        when(userService.findAllUsers(0, 10, "id", "ASC")).thenReturn(page);

        ResponseEntity<Page<UserResponse>> response = userController.getAllUsers(0, 10, "id", "ASC");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void deleteShouldReturnSuccessMessage() {
        when(userService.delete(1L)).thenReturn("User with id 1 deleted successfully.");

        String result = userController.delete(1L);

        assertEquals("User with id 1 deleted successfully.", result);
    }
}
