package com.hoangtien2k3.notificationservice.constant;

public class KafkaConstant {
    public static final String PROFILE_ONBOARDING_TOPIC = "profileOnboarding";
    public static final String PROFILE_ONBOARDED_TOPIC = "profileOnboarded";

    public static final String STATUS_PROFILE_PENDING = "PENDING";
    public static final String STATUS_PROFILE_ACTIVE = "ACTIVE";

    public static final String STATUS_PAYMENT_CREATING = "CREATING";
    public static final String STATUS_PAYMENT_REJECTED = "REJECTED";
    public static final String STATUS_PAYMENT_PROCESSING = "PROCESSING";
    public static final String STATUS_PAYMENT_SUCCESSFUL = "SUCCESSFUL";

    public static final String PAYMENT_REQUEST_TOPIC = "paymentRequest";
    public static final String PAYMENT_CREATED_TOPIC = "paymentCreated";
    public static final String PAYMENT_COMPLETED_TOPIC = "paymentCompleted";
}
