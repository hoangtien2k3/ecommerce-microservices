package com.hoangtien2k3.userservice.service;

import com.hoangtien2k3.userservice.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUser();
    User getUserById(Long id);
    User getUserByName(String userName);
    User saveUser(User user);
}
