package com.ecommerce.commonlib.viewmodel;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageResponseTest {

    @Test
    void computesTotalPagesAndFirstLast() {
        PageResponse<String> page = PageResponse.of(List.of("a", "b"), 0, 2, 5);

        assertThat(page.totalPages()).isEqualTo(3);
        assertThat(page.first()).isTrue();
        assertThat(page.last()).isFalse();
    }

    @Test
    void mapPreservesPagination() {
        PageResponse<String> page = PageResponse.of(List.of("1", "2"), 1, 2, 4);

        PageResponse<Integer> mapped = page.map(Integer::valueOf);

        assertThat(mapped.content()).containsExactly(1, 2);
        assertThat(mapped.page()).isEqualTo(1);
        assertThat(mapped.totalElements()).isEqualTo(4);
        assertThat(mapped.last()).isTrue();
    }

    @Test
    void handlesEmptyPage() {
        PageResponse<String> page = PageResponse.of(List.of(), 0, 10, 0);

        assertThat(page.totalPages()).isZero();
        assertThat(page.first()).isTrue();
        assertThat(page.last()).isTrue();
    }
}
