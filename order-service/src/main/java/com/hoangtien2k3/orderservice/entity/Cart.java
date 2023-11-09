package com.hoangtien2k3.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Builder
@Entity
@Table(name = "carts")
public record Cart(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "cart_id", unique = true, nullable = false, updatable = false)
        Integer cartId,
        @Column(name = "user_id")
        Integer userId,
        @JsonIgnore
        @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        Set<Order> orders
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
