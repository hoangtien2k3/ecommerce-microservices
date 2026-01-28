package com.hoangtien2k3.orderservice.api;

import com.hoangtien2k3.orderservice.dto.order.CartDto;
import com.hoangtien2k3.orderservice.service.CartService;
import com.hoangtien2k3.orderservice.service.CallAPI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/carts")
@Tag(name = "Cart Controller", description = "Cart management APIs")
public class CartController {

    private final CartService cartService;
    private final CallAPI testCallApi;

    @Operation(summary = "Get all carts", description = "Retrieve a list of all carts.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carts retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No content")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Mono<ResponseEntity<List<CartDto>>> findAll() {
        log.info("*** CartDto List, controller; fetch all categories *");
        return cartService.findAll()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(Collections.emptyList()));
    }

    @Operation(summary = "Get all carts with paging", description = "Retrieve a paginated list of all carts.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carts retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No content")
    })
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

    @Operation(summary = "Get cart by ID", description = "Retrieve cart information based on the provided ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @GetMapping("/{cartId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Mono<CartDto>> findById(@PathVariable("cartId")
                                                  @NotBlank(message = "Input must not be blank")
                                                  @Valid final String cartId) {
        log.info("*** CartDto, resource; fetch cart by id *");
        return ResponseEntity.ok(this.cartService.findById(Integer.parseInt(cartId)));
    }

    @Operation(summary = "Save cart", description = "Save a new cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cart created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ResponseEntity<CartDto>> save(@RequestBody
                                              @NotNull(message = "Input must not be NULL!")
                                              @Valid final CartDto cartDto) {
        log.info("*** CartDto, resource; save cart *");
        return cartService.save(cartDto)
                .map(cart -> ResponseEntity.status(HttpStatus.CREATED).body(cart))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @Operation(summary = "Update cart", description = "Update an existing cart.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart updated successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
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

    @Operation(summary = "Update cart by ID", description = "Update an existing cart based on the provided ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart updated successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
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

    @Operation(summary = "Delete cart by ID", description = "Delete a cart based on the provided ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @DeleteMapping("/{cartId}")
    @PreAuthorize("hasAuthority('USER')")
    public Mono<ResponseEntity<Boolean>> deleteById(@PathVariable("cartId") final String cartId) {
        log.info("*** Boolean, resource; delete cart by id *");
        return cartService.deleteById(Integer.parseInt(cartId))
                .thenReturn(ResponseEntity.ok(true))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(false));
    }
}