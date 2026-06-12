package com.ecommerce.commonlib.constants;

/**
 * Canonical API path prefixes. Every service must pin its {@code @RequestMapping}
 * to one of these constants so versioning stays consistent across the platform.
 */
public final class ApiPaths {

    public static final String API = "/api";
    public static final String V1 = "/v1";
    public static final String API_V1 = API + V1;

    // ---- Auth domain ----
    public static final String AUTH = API_V1 + "/auth";
    public static final String USERS = API_V1 + "/users";
    public static final String ROLES = API_V1 + "/roles";

    // ---- Catalog ----
    public static final String PRODUCTS = API_V1 + "/products";
    public static final String CATEGORIES = API_V1 + "/categories";

    // ---- Commerce ----
    public static final String CARTS = API_V1 + "/carts";
    public static final String ORDERS = API_V1 + "/orders";
    public static final String PAYMENTS = API_V1 + "/payments";
    public static final String INVENTORY = API_V1 + "/inventory";
    public static final String SHIPPINGS = API_V1 + "/shippings";
    public static final String FAVOURITES = API_V1 + "/favourites";
    public static final String MEDIAS = API_V1 + "/medias";

    // ---- Notifications ----
    public static final String NOTIFICATIONS = API_V1 + "/notifications";
    public static final String PAYMENT_NOTIFICATIONS = API_V1 + "/payment-notifications";
    public static final String EMAILS = API_V1 + "/emails";

    // ---- Storefront / Backoffice ----
    public static final String STOREFRONT_RATINGS = API_V1 + "/storefront/ratings";
    public static final String BACKOFFICE_RATINGS = API_V1 + "/backoffice/ratings";
    public static final String STOREFRONT_SEARCH = API_V1 + "/storefront/search";
    public static final String BACKOFFICE_PROMOTIONS = API_V1 + "/backoffice/promotions";
    public static final String BACKOFFICE_TAX_CLASSES = API_V1 + "/backoffice/tax-classes";
    public static final String BACKOFFICE_TAX_RATES = API_V1 + "/backoffice/tax-rates";

    private ApiPaths() {
    }
}
