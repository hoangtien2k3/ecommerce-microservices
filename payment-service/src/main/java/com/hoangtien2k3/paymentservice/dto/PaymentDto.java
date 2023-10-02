package com.hoangtien2k3.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoangtien2k3.paymentservice.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer paymentId;
    private Boolean isPayed;
    private PaymentStatus paymentStatus;

    @JsonProperty("order") // tên bảng hiển thị ở JSON
    @JsonInclude(JsonInclude.Include.NON_NULL) // nếu orderDto là null thì nó sẽ không được hiển thị ở JSON và ngược lại thì sẽ đươc hiển thị ở JSON
    private OrderDto orderDto;

}
