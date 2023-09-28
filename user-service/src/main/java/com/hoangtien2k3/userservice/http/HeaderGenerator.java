package com.hoangtien2k3.userservice.http;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class HeaderGenerator {

    public HttpHeaders getHeadersForSuccessGetMethod() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        return httpHeaders;
    }

    public HttpHeaders getHeadersForError() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/problem+json; charset=UTF-8");
        return httpHeaders;
    }

    public HttpHeaders getHeadersForSuccessPostMethod(HttpServletRequest request, Long newResourceId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        try {
            httpHeaders.setLocation(new URI(request.getRequestURI() + "/" + newResourceId));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return httpHeaders;
    }

}
