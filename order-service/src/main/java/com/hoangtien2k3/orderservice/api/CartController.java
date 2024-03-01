package com.hoangtien2k3.orderservice.api;

import com.hoangtien2k3.orderservice.dto.order.CartDto;
import com.hoangtien2k3.orderservice.service.CartService;
import com.hoangtien2k3.orderservice.service.CallAPI;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Tag(name = "CartController", description = "Operations related to carts")
public class CartController {

    private final CartService cartService;

    @Autowired
    private CallAPI testCallApi;

    @ApiOperation(value = "Get all carts", notes = "Retrieve a list of all carts.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Carts retrieved successfully", response = List.class),
            @ApiResponse(code = 204, message = "No content", response = ResponseEntity.class)
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Mono<ResponseEntity<List<CartDto>>> findAll() {
        log.info("*** CartDto List, controller; fetch all categories *");
        return cartService.findAll()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.ok(Collections.emptyList()));
    }

    @ApiOperation(value = "Get all carts with paging", notes = "Retrieve a paginated list of all carts.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Carts retrieved successfully", response = Page.class),
            @ApiResponse(code = 204, message = "No content", response = ResponseEntity.class)
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

    @ApiOperation(value = "Get cart by ID", notes = "Retrieve cart information based on the provided ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cart retrieved successfully", response = CartDto.class),
            @ApiResponse(code = 404, message = "Cart not found", response = ResponseEntity.class)
    })
    @GetMapping("/{cartId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Mono<CartDto>> findById(@PathVariable("cartId")
                                                  @NotBlank(message = "Input must not be blank")
                                                  @Valid final String cartId) {
        log.info("*** CartDto, resource; fetch cart by id *");
        return ResponseEntity.ok(this.cartService.findById(Integer.parseInt(cartId)));
    }


    @ApiOperation(value = "Save cart", notes = "Save a new cart.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cart saved successfully", response = CartDto.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ResponseEntity.class)
    })
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

    @ApiOperation(value = "Update cart", notes = "Update an existing cart.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cart updated successfully", response = CartDto.class),
            @ApiResponse(code = 404, message = "Cart not found", response = ResponseEntity.class)
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

    @ApiOperation(value = "Update cart by ID", notes = "Update an existing cart based on the provided ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cart updated successfully", response = CartDto.class),
            @ApiResponse(code = 404, message = "Cart not found", response = ResponseEntity.class)
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

    @ApiOperation(value = "Delete cart by ID", notes = "Delete a cart based on the provided ID.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Cart deleted successfully", response = Boolean.class),
            @ApiResponse(code = 404, message = "Cart not found", response = ResponseEntity.class)
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
