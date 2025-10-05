package com.kata.delivery.exception;

/**
 * Exception thrown when a time slot is not available for booking
 */
public class TimeSlotNotAvailableException extends RuntimeException {
    
    public TimeSlotNotAvailableException(String message) {
        super(message);
    }
    
    public TimeSlotNotAvailableException(Long timeSlotId) {
        super(String.format("Time slot with id %d is not available for booking", timeSlotId));
    }
}
