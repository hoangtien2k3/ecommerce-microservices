package com.ecommerce.commonlib.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Three-typed MapStruct contract for the {@code request → entity → response} pipeline that
 * controllers use during create/update operations.
 *
 * @param <M> the persistent entity
 * @param <V> the inbound view-model (request body)
 * @param <R> the outbound view-model (response body)
 */
public interface EntityCreateUpdateMapper<M, V, R> {

    M toModel(V vm);

    V toVm(M model);

    R toVmResponse(M model);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget M model, V vm);
}
