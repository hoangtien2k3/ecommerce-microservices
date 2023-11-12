package com.hoangtien2k3.proxyclient.business.auth.service;

import com.hoangtien2k3.proxyclient.business.auth.model.request.AuthenticationRequest;
import com.hoangtien2k3.proxyclient.business.auth.model.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse authenticate(final AuthenticationRequest authenticationRequest);
    Boolean authenticate(final String jwt);

}