package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.dto.order.CartDto;
import com.ecommerce.orderservice.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/carts")
@Tag(name = "CartController", description = "Operations related to carts")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<List<CartDto>> findAll() {
        log.info("*** CartDto List, controller; fetch all carts *");
        return ResponseEntity.ok(cartService.findAll());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Page<CartDto>> findAll(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "cartId") String sortBy,
                                                  @RequestParam(defaultValue = "asc") String sortOrder) {
        return ResponseEntity.ok(cartService.findAll(page, size, sortBy, sortOrder));
    }

    @GetMapping("/{cartId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<CartDto> findById(@PathVariable("cartId")
                                             @NotBlank(message = "Input must not be blank")
                                             @Valid final String cartId) {
        log.info("*** CartDto, resource; fetch cart by id *");
        return ResponseEntity.ok(cartService.findById(Integer.parseInt(cartId)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<CartDto> save(@RequestBody
                                         @NotNull(message = "Input must not be NULL!")
                                         @Valid final CartDto cartDto) {
        log.info("*** CartDto, resource; save cart *");
        return ResponseEntity.ok(cartService.save(cartDto));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CartDto> update(@RequestBody
                                           @NotNull(message = "Input must not be NULL")
                                           @Valid final CartDto cartDto) {
        log.info("*** CartDto, resource; update cart *");
        return ResponseEntity.ok(cartService.update(cartDto));
    }

    @PutMapping("/{cartId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<CartDto> update(@PathVariable("cartId")
                                           @NotBlank(message = "Input must not be blank")
                                           @Valid final String cartId,
                                           @RequestBody
                                           @NotNull(message = "Input must not be NULL")
                                           @Valid final CartDto cartDto) {
        log.info("*** CartDto, resource; update cart with cartId *");
        return ResponseEntity.ok(cartService.update(Integer.parseInt(cartId), cartDto));
    }

    @DeleteMapping("/{cartId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Boolean> deleteById(@PathVariable("cartId") final String cartId) {
        log.info("*** Boolean, resource; delete cart by id *");
        cartService.deleteById(Integer.parseInt(cartId));
        return ResponseEntity.ok(true);
    }
}
