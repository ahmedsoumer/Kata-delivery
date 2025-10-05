package com.kata.delivery.domain.port.inbound;

import com.kata.delivery.application.dto.TimeSlotResponse;
import com.kata.delivery.domain.DeliveryMode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Inbound port (use case) for time slot operations
 * Part of hexagonal architecture - defines business operations
 */
public interface TimeSlotUseCase {
    
    /**
     * Get all time slots
     */
    Flux<TimeSlotResponse> getAllTimeSlots();
    
    /**
     * Get time slot by ID
     */
    Mono<TimeSlotResponse> getTimeSlotById(Long id);
    
    /**
     * Get time slots by delivery mode
     */
    Flux<TimeSlotResponse> getTimeSlotsByDeliveryMode(DeliveryMode deliveryMode);
    
    /**
     * Get time slots by date
     */
    Flux<TimeSlotResponse> getTimeSlotsByDate(LocalDate date);
    
    /**
     * Get available time slots for a delivery mode and date
     */
    Flux<TimeSlotResponse> getAvailableTimeSlots(DeliveryMode deliveryMode, LocalDate date);
}
