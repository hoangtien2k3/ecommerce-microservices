package com.hoangtien2k3.userservice.api;

import com.hoangtien2k3.userservice.exception.wrapper.TokenErrorOrAccessTimeOut;
import com.hoangtien2k3.userservice.http.HeaderGenerator;
import com.hoangtien2k3.userservice.model.dto.request.ChangePasswordRequest;
import com.hoangtien2k3.userservice.model.dto.request.SignUp;
import com.hoangtien2k3.userservice.model.dto.request.UserDto;
import com.hoangtien2k3.userservice.model.dto.response.ResponseMessage;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/manager")
@Tag(name = "User Management", description = "User management APIs")
public class UserManager {
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final HeaderGenerator headerGenerator;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserManager(UserService userService, HeaderGenerator headerGenerator, JwtProvider jwtProvider,
                       ModelMapper modelMapper) {
        this.userService = userService;
        this.headerGenerator = headerGenerator;
        this.jwtProvider = jwtProvider;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Update user information", description = "Update the user information with the provided details.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PutMapping("update/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Mono<ResponseEntity<ResponseMessage>> update(@PathVariable("id") Long id,
                                                        @RequestBody SignUp updateDTO) {
        return userService.update(id, updateDTO)
                .flatMap(user -> Mono.just(new ResponseEntity<>(
                        new ResponseMessage("Update user: " + updateDTO.getUsername() + " successfully."),
                        HttpStatus.OK)))
                .onErrorResume(error -> Mono.just(new ResponseEntity<>(
                        new ResponseMessage("Update user: " + updateDTO.getUsername() + " failed " + error.getMessage()),
                        HttpStatus.BAD_REQUEST)));
    }

    @Operation(summary = "Change user password", description = "Change the password for the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Mono<String> changePassword(@RequestBody ChangePasswordRequest request) {
        return userService.changePassword(request);
    }

    @Operation(summary = "Delete user", description = "Delete a user with the specified ID.")
    @DeleteMapping("delete/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
    public Mono<String> delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }

    @Operation(summary = "Get user by username", description = "Retrieve user information based on the provided username.")
    @GetMapping("/user")
    @PreAuthorize("(isAuthenticated() and (hasAuthority('USER') and principal.username == #username) or hasAuthority('ADMIN'))")
    public Mono<ResponseEntity<UserDto>> getUserByUsername(@RequestParam(value = "username") String username) {
        return userService.findByUsername(username)
                .map(user -> modelMapper.map(user, UserDto.class))
                .map(userDto -> new ResponseEntity<>(userDto,
                        headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(null, headerGenerator.getHeadersForError(),
                        HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Get user by ID", description = "Retrieve user information based on the provided ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') and principal.id == #id")
    public Mono<ResponseEntity<UserDto>> getUserById(@PathVariable("id") Long id) {
        return userService.findById(id)
                .map(user -> modelMapper.map(user, UserDto.class))
                .map(userDto -> new ResponseEntity<>(userDto,
                        headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(null, headerGenerator.getHeadersForError(),
                        HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Get all users", description = "Retrieve all users with pagination", 
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<Page<UserDto>>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "id") String sortBy,
                                                           @RequestParam(defaultValue = "ASC") String sortOrder) {
        return userService.findAllUsers(page, size, sortBy, sortOrder)
                .map(usersPage -> new ResponseEntity<>(usersPage,
                        headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK));
    }

    @Operation(summary = "Get user information from token", description = "Retrieve user information based on the provided JWT token.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/info")
    public Mono<ResponseEntity<UserDto>> getUserInfo(@RequestHeader("Authorization") String token) {
        String username = jwtProvider.getUserNameFromToken(token);
        return userService.findByUsername(username)
                .map(user -> modelMapper.map(user, UserDto.class))
                .map(userDto -> new ResponseEntity<>(userDto,
                        headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK))
                .switchIfEmpty(Mono.error(new TokenErrorOrAccessTimeOut("Token error or access timeout")));
    }
}