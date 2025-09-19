package com.shakti.microservices.common_libs.Enums;

public enum OrderStatus {
    PLACED,             // Customer has placed the order
    CONFIRMED,          // Restaurant confirmed the order
    PREPARING,          // Restaurant is preparing the food
    READY_FOR_PICKUP,   // Order is ready to be picked by delivery agent
    PICKED_UP,          // Delivery agent picked up the order
    DELIVERED,          // Order delivered to customer
    CANCELLED           // Order cancelled by customer or restaurant
}