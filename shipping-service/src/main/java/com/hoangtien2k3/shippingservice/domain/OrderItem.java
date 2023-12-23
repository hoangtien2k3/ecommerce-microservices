package com.hoangtien2k3.shippingservice.domain;

import com.hoangtien2k3.shippingservice.domain.id.OrderItemId;
import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "order_items")
@IdClass(OrderItemId.class)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public final class OrderItem extends AbstractMappedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "product_id", nullable = false, updatable = false)
    private Integer productId;

    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private Integer orderId;

    @Column(name = "ordered_quantity")
    private Integer orderedQuantity;

}