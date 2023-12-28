package com.hoangtien2k3.userservice.service.impl;

import com.hoangtien2k3.userservice.exception.wrapper.RoleNotFoundException;
import com.hoangtien2k3.userservice.model.entity.Role;
import com.hoangtien2k3.userservice.model.entity.RoleName;
import com.hoangtien2k3.userservice.repository.IRoleRepository;
import com.hoangtien2k3.userservice.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements IRoleService {

    private final IRoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        return Optional.ofNullable(roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role Not Found with name: " + name)));
    }

}