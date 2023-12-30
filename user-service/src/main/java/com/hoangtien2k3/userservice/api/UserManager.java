package com.hoangtien2k3.userservice.api;

import com.hoangtien2k3.userservice.exception.wrapper.TokenErrorOrAccessTimeOut;
import com.hoangtien2k3.userservice.exception.wrapper.UserNotFoundException;
import com.hoangtien2k3.userservice.http.HeaderGenerator;
import com.hoangtien2k3.userservice.model.dto.model.TokenManager;
import com.hoangtien2k3.userservice.model.dto.request.ChangePasswordRequest;
import com.hoangtien2k3.userservice.model.dto.request.SignUpForm;
import com.hoangtien2k3.userservice.model.dto.response.ResponseMessage;
import com.hoangtien2k3.userservice.model.entity.User;
import com.hoangtien2k3.userservice.security.jwt.JwtProvider;
import com.hoangtien2k3.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final TokenManager tokenManager;
    private final HeaderGenerator headerGenerator;
    private final JwtProvider jwtProvider;

    @Autowired
    public UserManager(UserService userService, TokenManager tokenManager, HeaderGenerator headerGenerator, JwtProvider jwtProvider) {
        this.userService = userService;
        this.tokenManager = tokenManager;
        this.headerGenerator = headerGenerator;
        this.jwtProvider = jwtProvider;
    }

    @PutMapping("update/{id}")
    @PreAuthorize("isAuthenticated() and (hasAuthority('USER') and principal.username == #username)")
    public Mono<ResponseEntity<ResponseMessage>> update(@PathVariable("id") Long id, @RequestBody SignUpForm signUpForm) {
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
        Optional<User> user = Optional.ofNullable(userService.findByUsername(username)
                .orElseThrow(()
                        -> new UserNotFoundException("User not found with: " + username)
                ));
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
        Optional<User> user = userService.findById(id);
        return (user.isPresent())
                ? new ResponseEntity<>(user.get(), headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK)
                : new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<User> listUsers = userService.findAllUser()
                .orElseThrow(() ->
                        new UsernameNotFoundException("Not Found List User")
                );
        return new ResponseEntity<>(listUsers,
                headerGenerator.getHeadersForSuccessGetMethod(),
                HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<User> getUserInfo(@RequestHeader("Authorization") String token) {
        String username = jwtProvider.getUserNameFromToken(token);
        User user = userService.findByUsername(username)
                .orElseThrow(()
                        -> new TokenErrorOrAccessTimeOut("Token error or access timeout")
                );
        return new ResponseEntity<>(user,
                headerGenerator.getHeadersForSuccessGetMethod(),
                HttpStatus.OK);
    }

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        String token = tokenManager.TOKEN;
        return (token != null)
                ? ResponseEntity.ok(token)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found for the username.");
    }

}
