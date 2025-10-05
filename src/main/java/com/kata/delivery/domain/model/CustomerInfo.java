package com.kata.delivery.domain.model;

import lombok.Value;

/**
 * Value Object representing customer information (DDD pattern)
 * Immutable and contains validation logic
 */
@Value
public class CustomerInfo {
    String name;
    String email;
    
    public CustomerInfo(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.name = name;
        this.email = email;
    }
}
