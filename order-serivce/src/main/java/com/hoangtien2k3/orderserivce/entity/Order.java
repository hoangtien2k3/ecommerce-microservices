package com.hoangtien2k3.orderserivce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // date by order product
    @NotNull
    @Column(name = "ordered_date")
    private LocalDate orderedDate;

    @NotNull
    @Column(name = "status")
    private String status; // status order

    @Column(name = "total")
    private BigDecimal total; // total price product ordered

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "cart",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;


}
