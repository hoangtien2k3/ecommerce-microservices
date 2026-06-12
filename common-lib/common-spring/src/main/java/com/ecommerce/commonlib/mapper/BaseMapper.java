package com.ecommerce.commonlib.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Shared MapStruct contract for two-way model ↔ view-model mapping.
 *
 * @param <M> the entity (model) type
 * @param <V> the view-model type
 */
public interface BaseMapper<M, V> {

    M toModel(V vm);

    V toVm(M model);

    /**
     * Apply non-null fields from {@code vm} onto {@code model}. Null fields are skipped
     * to support HTTP PATCH semantics.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget M model, V vm);
}
