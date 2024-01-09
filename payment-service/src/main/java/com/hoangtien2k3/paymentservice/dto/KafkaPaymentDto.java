package com.hoangtien2k3.paymentservice.dto;

import com.hoangtien2k3.paymentservice.entity.PaymentStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Setter
@Getter
@Builder
public class KafkaPaymentDto {
    private Integer paymentId;
    private Boolean isPayed;
    private PaymentStatus paymentStatus;
    private Integer orderId;
    private Long userId;
}
