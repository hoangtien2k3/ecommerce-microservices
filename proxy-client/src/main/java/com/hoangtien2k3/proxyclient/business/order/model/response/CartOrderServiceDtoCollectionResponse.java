package com.hoangtien2k3.proxyclient.business.order.model.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

import com.hoangtien2k3.proxyclient.business.order.model.CartDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartOrderServiceDtoCollectionResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Collection<CartDto> collection;

}