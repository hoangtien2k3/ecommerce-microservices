package com.hoangtien2k3.orderservice.dto.response.collection;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DtoCollectionResponse<T> {

    private Collection<T> collection;

}