package com.hoangtien2k3.userservice.controller;

import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.service.UserService;
import com.hoangtien2k3.userservice.service.http.header.HeaderGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HeaderGenerator headerGenerator;

    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = userService.getAllUser();

        if (!users.isEmpty()) {
            return new ResponseEntity<List<User>>(users, headerGenerator.getHeadersSuccessGetMethod(), HttpStatus.OK);
        }
        return new ResponseEntity<List<User>>(users, headerGenerator.getHeadersError(), HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/users", params = "name")
    public ResponseEntity<User> getUserByName(@RequestParam("name") String username) {
        User user = userService.getUserByName(username);

        if (user != null) {
            return new ResponseEntity<User>(user, headerGenerator.getHeadersSuccessGetMethod(), HttpStatus.OK);
        }
        return new ResponseEntity<User>(headerGenerator.getHeadersError(), HttpStatus.NOT_FOUND);

    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return new ResponseEntity<User>(user, headerGenerator.getHeadersSuccessGetMethod(), HttpStatus.OK);
        }
        return new ResponseEntity<User>(headerGenerator.getHeadersError(), HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/users")
    public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request) {
        if (user != null)
            try {
                userService.saveUser(user);
                return new ResponseEntity<User>(user, headerGenerator.getHeadersSuccessPostMethod(request, user.getId()), HttpStatus.CREATED);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
    }

}
