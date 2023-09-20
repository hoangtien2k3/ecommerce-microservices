package com.hoangtien2k3.userservice.controller;

import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.service.UserService;
import com.hoangtien2k3.userservice.service.http.header.HeaderGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private HeaderGenerator headerGenerator;

    @PostMapping(value = "/registration")
    public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request){
        if(user != null)
            try {
                userService.saveUser(user);
                return new ResponseEntity<User>(user, headerGenerator.getHeadersSuccessPostMethod(request, user.getId()), HttpStatus.CREATED);
            }catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
    }

}
