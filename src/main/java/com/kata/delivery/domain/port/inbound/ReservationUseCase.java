package com.kata.delivery.domain.port.inbound;

import com.kata.delivery.application.dto.CreateReservationCommand;
import com.kata.delivery.application.dto.ReservationResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Inbound port (use case) for reservation operations
 * Part of hexagonal architecture - defines business operations
 */
public interface ReservationUseCase {
    
    /**
     * Create a new reservation
     */
    Mono<ReservationResponse> createReservation(CreateReservationCommand command);
    
    /**
     * Get reservation by ID
     */
    Mono<ReservationResponse> getReservationById(Long id);
    
    /**
     * Get all reservations
     */
    Flux<ReservationResponse> getAllReservations();
    
    /**
     * Get reservations by customer email
     */
    Flux<ReservationResponse> getReservationsByCustomer(String email);
    
    /**
     * Cancel a reservation
     */
    Mono<ReservationResponse> cancelReservation(Long id, String reason);
}
