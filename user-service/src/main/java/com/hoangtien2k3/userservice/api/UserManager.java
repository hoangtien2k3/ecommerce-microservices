package com.hoangtien2k3.userservice.api;

import com.hoangtien2k3.userservice.exception.wrapper.UserNotFoundException;
import com.hoangtien2k3.userservice.http.HeaderGenerator;
import com.hoangtien2k3.userservice.model.dto.model.TokenManager;
import com.hoangtien2k3.userservice.model.dto.request.SignUpForm;
import com.hoangtien2k3.userservice.model.dto.response.ResponseMessage;
import com.hoangtien2k3.userservice.model.entity.User;
import com.hoangtien2k3.userservice.repository.IUserRepository;
import com.hoangtien2k3.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/manager")
public class UserManager {

    private final IUserService userService;
    private final IUserRepository userRepository;
    private final TokenManager tokenManager;
    private final HeaderGenerator headerGenerator;

    @Autowired
    public UserManager(IUserService userService, IUserRepository userRepository, TokenManager tokenManager, HeaderGenerator headerGenerator) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.tokenManager = tokenManager;
        this.headerGenerator = headerGenerator;
    }

    @PutMapping("update/{userId}")
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ResponseEntity<ResponseMessage>> update(@PathVariable("userId") Long userId, @RequestBody SignUpForm signUpForm) {
        return userService.update(userId, signUpForm)
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

    @DeleteMapping("delete/{userId}")
    @PreAuthorize("hasAuthority('USER')")
    public String delete(@PathVariable("userId") Long userId) {
        return userService.delete(userId);
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated() and ((hasAuthority('USER') and principal.username == #username) or hasAuthority('ADMIN'))")
    public ResponseEntity<?> getUserByUsername(@RequestParam(value = "username") String username) {
        Optional<User> user = Optional.ofNullable(userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with: " + username)));
        return user.map(u -> new ResponseEntity<>(u, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('USER')")
    //    @PreAuthorize("hasAuthority('ADMIN') or principal.id == #userId")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        Optional<User> user = userService.findById(id);
        return (user.isPresent())
                ? new ResponseEntity<>(user.get(), headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK)
                : new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);

    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<User> listUsers = userService.findAllUser()
                .orElseThrow(() -> new UsernameNotFoundException("Not Found List User"));
        return new ResponseEntity<>(listUsers,
                headerGenerator.getHeadersForSuccessGetMethod(),
                HttpStatus.OK);
    }

    @PreAuthorize(value = "hasAuthority('USER')")
    @GetMapping("/token/{username}")
    public ResponseEntity<String> getTokenByUsername(@PathVariable("username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));

        String token = null;
        if (user != null) {
            token = tokenManager.getTokenByUsername(user.getUsername());
        }

        return (token != null)
                ? ResponseEntity.ok(token)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found for the username.");
    }

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        String token = tokenManager.TOKEN;
        return (token != null)
                ? ResponseEntity.ok(token)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found for the username.");
    }

}
