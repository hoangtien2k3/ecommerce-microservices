package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.model.dto.request.ChangePasswordRequest;
import com.ecommerce.authservice.model.dto.request.SignUp;
import com.ecommerce.authservice.model.dto.request.UserDto;
import com.ecommerce.authservice.model.dto.response.ResponseMessage;
import com.ecommerce.authservice.service.UserService;
import com.ecommerce.commonlib.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/manager")
public class UserManager {
        private final ModelMapper modelMapper;

        private final UserService userService;

        @Autowired
        public UserManager(UserService userService, ModelMapper modelMapper) {
                this.userService = userService;
                this.modelMapper = modelMapper;
        }

        @PutMapping("update/{id}")
        @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
        public ResponseEntity<ResponseMessage> update(@PathVariable("id") Long id,
                        @RequestBody SignUp updateDTO) {
                userService.update(id, updateDTO);
                return ResponseEntity.status(HttpStatus.OK)
                                .body(ResponseMessage.builder()
                                                .message("Update user successfully.")
                                                .build());
        }

        @PutMapping("/change-password")
        @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
        public String changePassword(@RequestBody ChangePasswordRequest request) {
                return userService.changePassword(request);
        }

        @DeleteMapping("delete/{id}")
        @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
        public String delete(@PathVariable("id") Long id) {
                return userService.delete(id);
        }

        @GetMapping("/user")
        @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
        public ResponseEntity<UserDto> getUserByUsername(@RequestParam(value = "username") String username) {
                UserDto userDto = modelMapper.map(userService.findByUsername(username), UserDto.class);
                return ResponseEntity.ok(userDto);
        }

        @GetMapping("/user/{id}")
        @PreAuthorize("isAuthenticated() and (hasAuthority('USER') or hasAuthority('ADMIN'))")
        public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
                UserDto userDto = modelMapper.map(userService.findById(id), UserDto.class);
                return ResponseEntity.ok(userDto);
        }

        @GetMapping("/all")
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<Page<UserDto>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "ASC") String sortOrder) {

                return ResponseEntity.ok(userService.findAllUsers(page, size, sortBy, sortOrder));
        }

        @GetMapping("/info")
        public ResponseEntity<UserDto> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
                if (jwt == null) {
                        throw BusinessException.unauthorized("Token error or access timeout");
                }
                String username = jwt.getSubject();
                UserDto userDto = modelMapper.map(userService.findByUsername(username), UserDto.class);
                return ResponseEntity.ok(userDto);
        }

}
