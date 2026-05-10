package com.ecommerce.userservice.api;

import com.ecommerce.userservice.model.dto.request.Login;
import com.ecommerce.userservice.model.dto.request.SignUp;
import com.ecommerce.userservice.model.dto.response.TokenValidationResponse;
import com.ecommerce.userservice.model.dto.response.InformationMessage;
import com.ecommerce.userservice.model.dto.response.JwtResponseMessage;
import com.ecommerce.userservice.model.dto.response.ResponseMessage;
import com.ecommerce.userservice.security.validate.AuthorityTokenUtil;
import com.ecommerce.userservice.service.UserService;
import com.ecommerce.userservice.security.validate.TokenValidate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuth {

    private final UserService userService;
    private final TokenValidate tokenValidate;
    private final AuthorityTokenUtil authorityTokenUtil;

    @PostMapping({ "/signup", "/register" })
    public Mono<ResponseMessage> register(@Valid @RequestBody SignUp signUp) {
        return userService.register(signUp)
                .map(user -> new ResponseMessage("Create user: " + signUp.getUsername() + " successfully."))
                .onErrorResume(error -> Mono.just(new ResponseMessage("Error occurred while creating the account.")));
    }

    @PostMapping({ "/signin", "/login" })
    public Mono<ResponseEntity<JwtResponseMessage>> login(@Valid @RequestBody Login signInForm) {
        return userService.login(signInForm)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    JwtResponseMessage errorjwtResponseMessage = new JwtResponseMessage(
                            null,
                            null,
                            new InformationMessage());
                    return Mono.just(new ResponseEntity<>(errorjwtResponseMessage, HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Mono<ResponseEntity<String>> logout() {
        log.info("Logout endpoint called");
        return userService.logout()
                .then(Mono.just(new ResponseEntity<>("Logged out successfully.", HttpStatus.OK)))
                .onErrorResume(error -> {
                    log.error("Logout failed", error);
                    return Mono.just(new ResponseEntity<>("Logout failed.", HttpStatus.BAD_REQUEST));
                });
    }

    @GetMapping({ "/validateToken", "/validate-token" })
    public ResponseEntity<?> validateToken(@RequestHeader(name = "Authorization") String authorizationToken) {
        if (tokenValidate.validateToken(authorizationToken)) {
            return ResponseEntity.ok(new TokenValidationResponse("Valid token"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenValidationResponse("Invalid token"));
        }
    }

    @GetMapping({ "/hasAuthority", "/authorization" })
    public ResponseEntity<?> getAuthority(@RequestHeader(name = "Authorization") String authorizationToken,
            @RequestParam String requiredRole) {
        List<String> authorities = authorityTokenUtil.checkPermission(authorizationToken);

        if (authorities.contains(requiredRole)) {
            return ResponseEntity.ok(new TokenValidationResponse("Role access api"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponse("Invalid token"));
        }
    }

}
