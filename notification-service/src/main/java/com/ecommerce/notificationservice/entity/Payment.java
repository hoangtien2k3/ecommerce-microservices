package com.ecommerce.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer paymentId;
    private Boolean isPayed;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Integer orderId;
    private Long userId;

}
