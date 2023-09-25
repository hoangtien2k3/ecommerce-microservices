package com.hoangtien2k3.userservice.controller;

import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.http.HeaderGenerator;
import com.hoangtien2k3.userservice.repository.IUserRepository;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/admin")
public class AdminUserController {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private HeaderGenerator headerGenerator;

    @GetMapping(value = "/user/{userName}")
    public ResponseEntity<?> getAllUser(@PathVariable("userName") String username) {

        User user = userRepository.getUserByUsername(username);
        if (user != null) {
            return new ResponseEntity<>(user,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(null,
                headerGenerator.getHeadersForError(),
                HttpStatus.NOT_FOUND);
    }

}
