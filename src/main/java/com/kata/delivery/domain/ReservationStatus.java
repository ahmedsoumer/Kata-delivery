package com.kata.delivery.domain;

/**
 * Enum representing the status of a reservation
 */
public enum ReservationStatus {
    /**
     * Reservation is confirmed
     */
    CONFIRMED,
    
    /**
     * Reservation has been cancelled
     */
    CANCELLED,
    
    /**
     * Reservation is pending confirmation
     */
    PENDING
}
