package com.hoangtien2k3.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Size;

@AllArgsConstructor
public class MessageResponse {
    @Size(min = 10, max = 500)
    private String message;
}
