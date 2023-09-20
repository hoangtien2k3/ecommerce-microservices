package com.hoangtien2k3.orderserivce.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode // generate equals() and hashcode() method
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "quantity")
    @NotNull
    private int quantity;

    @Column(name = "subtotal")
    @NotNull
    private BigDecimal subtotal;




}
