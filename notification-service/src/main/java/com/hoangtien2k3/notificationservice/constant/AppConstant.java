package com.hoangtien2k3.notificationservice.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AppConstant {

    public static final String LOCAL_DATE_FORMAT = "dd-MM-yyyy";
    public static final String LOCAL_DATE_TIME_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";
    public static final String ZONED_DATE_TIME_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";
    public static final String INSTANT_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public abstract static class DiscoveredDomainsApi {

        public static final String USER_SERVICE_HOST = "http://USER-SERVICE/user-service";
//        public static final String USER_SERVICE_API_URL = "http://USER-SERVICE/user-service/api/users";
        public static final String USER_SERVICE_API_URL = "http://localhost:8088/api/manager";

        public static final String PRODUCT_SERVICE_HOST = "http://PRODUCT-SERVICE/product-service";
        public static final String PRODUCT_SERVICE_API_URL = "http://PRODUCT-SERVICE/product-service/api/products";

        public static final String ORDER_SERVICE_HOST = "http://ORDER-SERVICE/order-service";
        public static final String ORDER_SERVICE_API_URL = "http://ORDER-SERVICE/order-service/api/orders";

        public static final String FAVOURITE_SERVICE_HOST = "http://FAVOURITE-SERVICE/favourite-service";
        public static final String FAVOURITE_SERVICE_API_URL = "http://FAVOURITE-SERVICE/favourite-service/api/favourites";

        public static final String PAYMENT_SERVICE_HOST = "http://PAYMENT-SERVICE/payment-service";
        public static final String PAYMENT_SERVICE_API_URL = "http://PAYMENT-SERVICE/payment-service/api/payments";

        public static final String SHIPPING_SERVICE_HOST = "http://SHIPPING-SERVICE/shipping-service";
        public static final String SHIPPING_SERVICE_API_URL = "http://SHIPPING-SERVICE/shipping-service/api/shippings";

    }

}
