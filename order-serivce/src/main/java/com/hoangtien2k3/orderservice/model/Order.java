package com.hoangtien2k3.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orderNumber")
    @NotBlank
    private String orderNumber;

    @OneToMany(cascade = CascadeType.ALL)
    @Column(name = "orderLineItemsList")
    private List<OrderLineItems> orderLineItemsList;

}
