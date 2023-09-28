package com.hoangtien2k3.orderservice.controller;

import com.hoangtien2k3.orderservice.dto.request.OrderRequest;
import com.hoangtien2k3.orderservice.dto.response.LoginResponse;
import com.hoangtien2k3.orderservice.dto.response.MessageResponse;
import com.hoangtien2k3.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Value("${user.service.url}") // URL máy chủ user-service
    private String userServiceUrl;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate; // Inject RestTemplate

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest,
                             @RequestHeader("Authorization") String authorizationHeader) {

        // Gửi yêu cầu đăng nhập và lấy token từ user-service
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.add("Authorization", authorizationHeader);

        // Thêm các header khác nếu cần
        HttpEntity<String> loginRequestEntity = new HttpEntity<>(loginHeaders);

        ResponseEntity<LoginResponse> loginResponseEntity = restTemplate.exchange(
                userServiceUrl + "/api/auth/signin",
                HttpMethod.POST,
                loginRequestEntity,
                LoginResponse.class
        );

        // Xử lý kết quả từ responseEntity
        LoginResponse loginResponse = loginResponseEntity.getBody();

        // Kiểm tra xem cuộc gọi đăng nhập đã thành công hay không và bạn có thể sử dụng token được trả về
        if (loginResponseEntity.getStatusCode() == HttpStatus.OK && loginResponse != null) {
            String token = loginResponse.getToken();

            // Tiếp tục với việc gọi orderService và truyền token
            orderService.placeOrderWithToken(orderRequest, token);

            return "Order Placed Successfully";
        } else {
            // Xử lý lỗi khi không thể đăng nhập và lấy token
            throw new RuntimeException("Failed to obtain access token from user-service");
        }
    }
}