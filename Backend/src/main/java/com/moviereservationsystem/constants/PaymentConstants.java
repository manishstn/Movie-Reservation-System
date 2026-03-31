package com.moviereservationsystem.constants;

public class PaymentConstants {

    // Gateway Names (Match the 'gateway_name' column in your DB)
    public static final String GATEWAY_RAZORPAY = "RAZORPAY";
    public static final String GATEWAY_STRIPE = "STRIPE";
    public static final String GATEWAY_PAYPAL = "PAYPAL";

    // Payment Statuses (Match your PaymentStatus Enum)
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_REFUNDED = "REFUNDED";

    // Currencies
    public static final String CURRENCY_INR = "INR";
    public static final String CURRENCY_USD = "USD";

    // Error Messages
    public static final String ERR_PAYMENT_FAILED = "Payment processing failed. Please try again.";
    public static final String ERR_GATEWAY_INACTIVE = "Selected payment gateway is currently unavailable.";
}