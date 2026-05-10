package com.ecommerce.userservice.api;

import com.ecommerce.userservice.exception.wrapper.TokenErrorOrAccessTimeOut;
import com.ecommerce.userservice.http.HeaderGenerator;
import com.ecommerce.userservice.model.dto.request.ChangePasswordRequest;
import com.ecommerce.userservice.model.dto.request.SignUp;
import com.ecommerce.userservice.model.dto.request.UserDto;
import com.ecommerce.userservice.model.dto.response.ResponseMessage;
import com.ecommerce.userservice.security.jwt.JwtProvider;
import com.ecommerce.userservice.service.UserService;
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

        @PutMapping("update/{id}")
        @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
        public Mono<ResponseEntity<ResponseMessage>> update(@PathVariable("id") Long id,
                        @RequestBody SignUp updateDTO) {
                return userService.update(id, updateDTO)
                                .flatMap(user -> Mono.just(new ResponseEntity<>(
                                                new ResponseMessage("Update user: " + updateDTO.getUsername()
                                                                + " successfully."),
                                                HttpStatus.OK)))
                                .onErrorResume(
                                                error -> Mono.just(new ResponseEntity<>(
                                                                new ResponseMessage("Update user: "
                                                                                + updateDTO.getUsername() + " failed "
                                                                                + error.getMessage()),
                                                                HttpStatus.BAD_REQUEST)));
        }

        @PutMapping("/change-password")
        @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
        public Mono<String> changePassword(@RequestBody ChangePasswordRequest request) {
                return userService.changePassword(request);
        }

        @DeleteMapping("delete/{id}")
        @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
        public Mono<String> delete(@PathVariable("id") Long id) {
                return userService.delete(id);
        }

        @GetMapping("/user")
        @PreAuthorize("(isAuthenticated() and (hasAuthority('USER') and principal.username == #username) or hasAuthority('ADMIN'))")
        public Mono<?> getUserByUsername(@RequestParam(value = "username") String username) {
                return userService.findByUsername(username)
                                .map(user -> {
                                        UserDto userDto = modelMapper.map(user, UserDto.class);
                                        return new ResponseEntity<UserDto>(userDto,
                                                headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
                                })
                                .defaultIfEmpty(new ResponseEntity<>(null, headerGenerator.getHeadersForError(),
                                                HttpStatus.NOT_FOUND))
                                .map(r -> (Object)r);
        }

        @GetMapping("/user/{id}")
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') and principal.id == #id")
        public Mono<?> getUserById(@PathVariable("id") Long id) {
                return userService.findById(id)
                                .map(user -> {
                                        UserDto userDto = modelMapper.map(user, UserDto.class);
                                        return new ResponseEntity<UserDto>(userDto,
                                                headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
                                })
                                .defaultIfEmpty(new ResponseEntity<>(null, headerGenerator.getHeadersForError(),
                                                HttpStatus.NOT_FOUND))
                                .map(r -> (Object)r);
        }

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

        @GetMapping("/info")
        public Mono<?> getUserInfo(@RequestHeader("Authorization") String token) {
                String username = jwtProvider.getUserNameFromToken(token);
                return userService.findByUsername(username)
                                .map(user -> modelMapper.map(user, UserDto.class))
                                .map(userDto -> new ResponseEntity<>(userDto,
                                                headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK))
                                .switchIfEmpty(Mono
                                                .error(new TokenErrorOrAccessTimeOut("Token error or access timeout")));
        }

}
