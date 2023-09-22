package com.hoangtien2k3.userservice.service;

import com.hoangtien2k3.userservice.entity.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> findByUsername(String name);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    User save(User user);
}
