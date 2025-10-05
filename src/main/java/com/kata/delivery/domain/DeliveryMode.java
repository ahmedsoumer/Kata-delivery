package com.kata.delivery.domain;

/**
 * Enum representing the available delivery modes
 */
public enum DeliveryMode {
    /**
     * Drive - Customer picks up at store
     */
    DRIVE,
    
    /**
     * Standard delivery - Scheduled delivery
     */
    DELIVERY,
    
    /**
     * Same day delivery
     */
    DELIVERY_TODAY,
    
    /**
     * ASAP delivery - As soon as possible
     */
    DELIVERY_ASAP
}
