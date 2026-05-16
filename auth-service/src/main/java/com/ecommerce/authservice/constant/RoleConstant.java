package com.ecommerce.authservice.constant;

public final class RoleConstant {
    public static final String ROLE_USER = "USER";
    public static final String ROLE_PM = "PM";
    public static final String ROLE_ADMIN = "ADMIN";

    public static final String DEFAULT_ROLE = ROLE_USER;

    public static final int ROLE_NAME_MIN_LENGTH = 2;
    public static final int ROLE_NAME_MAX_LENGTH = 60;

    private RoleConstant() {
    }
}
