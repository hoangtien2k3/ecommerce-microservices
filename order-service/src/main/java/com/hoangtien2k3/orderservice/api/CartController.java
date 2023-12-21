package com.hoangtien2k3.orderservice.api;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.hoangtien2k3.orderservice.dto.CartDto;
import com.hoangtien2k3.orderservice.dto.response.collection.DtoCollectionResponse;
import com.hoangtien2k3.orderservice.security.JwtValidate;
import com.hoangtien2k3.orderservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private final CartService cartService;

    @Autowired
    private final JwtValidate jwtValidate;

    @GetMapping
    public ResponseEntity<DtoCollectionResponse<CartDto>> findAll() {
        log.info("CartDto List, controller; fetch all categories");
        return ResponseEntity.ok(new DtoCollectionResponse<>(this.cartService.findAll()));
    }


    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> findById(@RequestHeader(name = "Authorization") String authorizationHeader,
                                            @PathVariable("cartId")
                                            @NotBlank(message = "Input must not be blank")
                                            @Valid final Integer cartId) {

        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            log.info("CartDto, resource; fetch cart by id");
            return ResponseEntity.ok(this.cartService.findById(cartId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping
    public ResponseEntity<CartDto> save(@RequestHeader(name = "Authorization") String authorizationHeader,
                                        @RequestBody
                                        @NotNull(message = "Input must not be NULL!")
                                        @Valid final CartDto cartDto) {

        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("CartDto, resource; save cart");
        return ResponseEntity.ok(this.cartService.save(cartDto));
    }

    @PutMapping
    public ResponseEntity<CartDto> update(@RequestHeader(name = "Authorization") String authorizationHeader,
                                          @RequestBody
                                          @NotNull(message = "Input must not be NULL")
                                          @Valid final CartDto cartDto) {
        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("CartDto, resource; update cart");
        return ResponseEntity.ok(this.cartService.update(cartDto));
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<CartDto> update(@RequestHeader(name = "Authorization") String authorizationHeader,
                                          @PathVariable("cartId")
                                          @NotBlank(message = "Input must not be blank")
                                          @Valid final Integer cartId,
                                          @RequestBody
                                          @NotNull(message = "Input must not be NULL")
                                          @Valid final CartDto cartDto) {
        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("CartDto, resource; update cart with cartId");
        return ResponseEntity.ok(this.cartService.update(cartId, cartDto));
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Boolean> deleteById(@RequestHeader(name = "Authorization") String authorizationHeader,
                                              @PathVariable("cartId") final Integer cartId) {
        if (!jwtValidate.validateTokenUserService(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("Boolean, resource; delete cart by id");
        this.cartService.deleteById(cartId);
        return ResponseEntity.ok(true);
    }

}
