package com.hoangtien2k3.userservice.service.impl;

import com.hoangtien2k3.userservice.config.JwtToken;
import com.hoangtien2k3.userservice.dao.ResponseData;
import com.hoangtien2k3.userservice.entity.User;
import com.hoangtien2k3.userservice.entity.UserRole;
import com.hoangtien2k3.userservice.repository.UserRepository;
import com.hoangtien2k3.userservice.repository.UserRoleRepository;
import com.hoangtien2k3.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtToken token;

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


    @Override
    public ResponseData<String> loginUser(String userName, String userPassword) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName, userPassword)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = token.generateToken((UserDetails) authentication.getPrincipal());

        return new ResponseData(HttpStatus.OK, "Login Successfully", jwt);

    }


}
