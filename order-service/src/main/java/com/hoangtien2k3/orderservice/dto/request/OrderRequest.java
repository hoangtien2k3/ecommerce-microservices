package com.hoangtien2k3.orderservice.dto.request;

import com.hoangtien2k3.orderservice.dto.OrderItemsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private List<OrderItemsDto> orderItemsDtoList;

}
