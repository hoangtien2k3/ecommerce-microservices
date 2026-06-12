package com.ecommerce.commonlib.viewmodel;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.function.Function;

/**
 * Lightweight page envelope used for paginated endpoints. Decouples the wire format
 * from Spring Data's {@code Page} (which is not designed to be serialized directly).
 *
 * <h3>Why not return Spring {@code Page<T>} directly?</h3>
 * The {@code Page<T>} JSON shape changed between Spring Boot 2 and 3 (now wrapped in
 * {@code PageImpl}) and is officially flagged as unstable. This record pins the shape
 * the platform exposes externally.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static <T> PageResponse<T> of(List<T> content,
                                         int page,
                                         int size,
                                         long totalElements) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                page + 1 >= totalPages
        );
    }

    /**
     * Map the content elements while keeping pagination metadata intact.
     * Avoid building a Spring {@code Page} on the caller side just to remap.
     */
    public <R> PageResponse<R> map(Function<? super T, ? extends R> mapper) {
        List<R> mapped = content.stream().<R>map(mapper).toList();
        return new PageResponse<>(mapped, page, size, totalElements, totalPages, first, last);
    }
}
