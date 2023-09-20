package com.hoangtien2k3.userservice.service.impl;

import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.entity.UserRole;
import com.hoangtien2k3.userservice.repository.UserRepository;
import com.hoangtien2k3.userservice.repository.UserRoleRepository;
import com.hoangtien2k3.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {

        return userRepository.getOne(id);
    }

    @Override
    public User getUserByName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User saveUser(User user) {

        user.setActive(1); // active success
        UserRole role = userRoleRepository.findUserRoleByRoleName("ROLE_USER");
        user.setRole(role);

        return userRepository.save(user);
    }
}
