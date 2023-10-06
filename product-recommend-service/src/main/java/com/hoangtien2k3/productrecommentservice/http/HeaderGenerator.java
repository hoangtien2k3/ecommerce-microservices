package com.hoangtien2k3.productrecommentservice.http;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@Slf4j
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
        try {
            httpHeaders.setLocation(new URI(request.getRequestURI() + "/" + newResourceId));
        } catch (URISyntaxException e) {
            log.error("Error is " + e);
        }
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        return httpHeaders;
    }
}