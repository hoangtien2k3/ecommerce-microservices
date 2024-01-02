package com.hoangtien2k3.userservice.api;

import com.hoangtien2k3.userservice.exception.wrapper.TokenErrorOrAccessTimeOut;
import com.hoangtien2k3.userservice.exception.wrapper.UserNotFoundException;
import com.hoangtien2k3.userservice.http.HeaderGenerator;
import com.hoangtien2k3.userservice.model.dto.model.TokenManager;
import com.hoangtien2k3.userservice.model.dto.request.ChangePasswordRequest;
import com.hoangtien2k3.userservice.model.dto.request.UserDTO;
import com.hoangtien2k3.userservice.model.dto.response.ResponseMessage;
import com.hoangtien2k3.userservice.model.entity.User;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/manager")
public class UserManager {

    private final UserService userService;
    private final HeaderGenerator headerGenerator;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserManager(UserService userService, HeaderGenerator headerGenerator, JwtProvider jwtProvider) {
        this.userService = userService;
        this.headerGenerator = headerGenerator;
        this.jwtProvider = jwtProvider;
    }

    @PutMapping("update/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') and principal.username == #username)")
    public Mono<ResponseEntity<ResponseMessage>> update(@PathVariable("id") Long id, @RequestBody UserDTO signUpForm) {
        return userService.update(id, signUpForm)
                .flatMap(user -> Mono.just(new ResponseEntity<>(
                        new ResponseMessage("Update user: " + signUpForm.getUsername() + " successfully."),
                        HttpStatus.OK))
                )
                .onErrorResume(
                        error -> Mono.just(new ResponseEntity<>(
                                new ResponseMessage("Update user: " + signUpForm.getUsername() + " failed " + error.getMessage()),
                                HttpStatus.BAD_REQUEST)
                        )
                );
    }

    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Mono<ResponseEntity<ResponseMessage>> changePassword(@RequestBody ChangePasswordRequest request) {
        return userService.changePassword(request)
                .map(user -> new ResponseEntity<>(new ResponseMessage("Change password user successfully."), HttpStatus.OK))
                .onErrorResume(error -> {
                    log.error("Change password failed: {}", error.getMessage(), error);
                    return Mono.just(new ResponseEntity<>(new ResponseMessage("Change password failed."), HttpStatus.BAD_REQUEST));
                });
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("isAuthenticated() and ((hasAuthority('USER') and principal.username == #username) or hasAuthority('ADMIN'))")
    public String delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }

    @GetMapping("/user")
    @PreAuthorize("((hasAuthority('USER') and principal.username == #username) or hasAuthority('ADMIN'))")
    public ResponseEntity<?> getUserByUsername(@RequestParam(value = "username") String username) {
        Optional<UserDTO> user = Optional.ofNullable(userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with: " + username)));
        return user.map(u -> new ResponseEntity<>(u,
                        headerGenerator.getHeadersForSuccessGetMethod(),
                        HttpStatus.OK)
                )
                .orElseGet(() -> new ResponseEntity<>(null,
                        headerGenerator.getHeadersForError(),
                        HttpStatus.NOT_FOUND)
                );
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('USER') and principal.id == #id)")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        Optional<UserDTO> userDTO = Optional.ofNullable(userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with: " + id)));
        return (userDTO.isPresent())
                ? new ResponseEntity<>(userDTO.get(), headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK)
                : new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder) {

        Page<UserDTO> usersPage = userService.findAllUsers(page, size, sortBy, sortOrder);
        return new ResponseEntity<>(usersPage, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        String username = jwtProvider.getUserNameFromToken(token);
        UserDTO user = userService.findByUsername(username)
                .orElseThrow(() -> new TokenErrorOrAccessTimeOut("Token error or access timeout"));

        return (user != null)
                ? new ResponseEntity<>(user, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK)
                : new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

}
