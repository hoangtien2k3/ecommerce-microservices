package com.hoangtien2k3.orderservice.api;

import com.hoangtien2k3.orderservice.dto.CartDto;
import com.hoangtien2k3.orderservice.service.CartService;
import com.hoangtien2k3.orderservice.service.CallAPI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/carts")
@Tag(name = "CartController", description = "To perform operations on students")
public class CartController {

    private final CartService cartService;

    @Autowired
    private CallAPI testCallApi;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Mono<ResponseEntity<List<CartDto>>> findAll() {
        log.info("*** CartDto List, controller; fetch all categories *");
        return cartService.findAll()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(Collections.emptyList()));
    }

    // paging
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Mono<ResponseEntity<Page<CartDto>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(defaultValue = "cartId") String sortBy,
                                                       @RequestParam(defaultValue = "asc") String sortOrder) {
        return cartService.findAll(page, size, sortBy, sortOrder)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/{cartId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Mono<CartDto>> findById(@PathVariable("cartId")
                                                  @NotBlank(message = "Input must not be blank")
                                                  @Valid final String cartId) {
        log.info("*** CartDto, resource; fetch cart by id *");
        return ResponseEntity.ok(this.cartService.findById(Integer.parseInt(cartId)));
    }

    @Operation(
            summary = "POST operation on cart",
            description = "It is used to save cart object in database"
    )
    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ResponseEntity<CartDto>> save(@RequestBody
                                              @NotNull(message = "Input must not be NULL!")
                                              @Valid final CartDto cartDto) {
        log.info("*** CartDto, resource; save cart *");
        return cartService.save(cartDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<CartDto>> update(@RequestBody
                                                @NotNull(message = "Input must not be NULL")
                                                @Valid final CartDto cartDto) {
        log.info("*** CartDto, resource; update cart *");
        return cartService.update(cartDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{cartId}")
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ResponseEntity<CartDto>> update(@PathVariable("cartId")
                                                @NotBlank(message = "Input must not be blank")
                                                @Valid final String cartId,
                                                @RequestBody
                                                @NotNull(message = "Input must not be NULL")
                                                @Valid final CartDto cartDto) {
        log.info("*** CartDto, resource; update cart with cartId *");
        return cartService.update(Integer.parseInt(cartId), cartDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{cartId}")
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ResponseEntity<Boolean>> deleteById(@PathVariable("cartId") final String cartId) {
        log.info("*** Boolean, resource; delete cart by id *");
        return cartService.deleteById(Integer.parseInt(cartId))
                .thenReturn(ResponseEntity.ok(true))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }

}
