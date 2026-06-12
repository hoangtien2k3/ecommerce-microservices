package com.ecommerce.commonlib.constants;

/**
 * Default pagination knobs used by {@code @RequestParam} defaults on list endpoints.
 */
public final class PageableConstant {

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final int MAX_PAGE_SIZE = 200;

    private PageableConstant() {
    }
}
