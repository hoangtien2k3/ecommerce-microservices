package com.hoangtien2k3.proxyclient.business.auth.service.impl;

import com.hoangtien2k3.proxyclient.constant.AppConstant;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hoangtien2k3.proxyclient.business.user.model.CredentialDto;
import com.hoangtien2k3.proxyclient.business.user.model.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String API_URL = AppConstant.DiscoveredDomainsApi.USER_SERVICE_HOST + "/api/credentials";
    private final RestTemplate restTemplate;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        log.info("UserDetails, load user by username");
        return new UserDetailsImpl(this.restTemplate.getForObject(API_URL + "/username/" + username, CredentialDto.class));
    }



}