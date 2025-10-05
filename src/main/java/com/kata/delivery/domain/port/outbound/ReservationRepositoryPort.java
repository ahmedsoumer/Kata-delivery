package com.kata.delivery.domain.port.outbound;

import com.kata.delivery.domain.ReservationStatus;
import com.kata.delivery.domain.model.ReservationAggregate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port (interface) for Reservation persistence
 * Part of hexagonal architecture - domain defines the contract
 */
public interface ReservationRepositoryPort {
    
    Mono<ReservationAggregate> save(ReservationAggregate reservation);
    
    Mono<ReservationAggregate> findById(Long id);
    
    Flux<ReservationAggregate> findAll();
    
    Flux<ReservationAggregate> findByCustomerEmail(String email);
    
    Flux<ReservationAggregate> findByStatus(ReservationStatus status);
    
    Flux<ReservationAggregate> findByTimeSlotId(Long timeSlotId);
    
    Mono<Void> deleteById(Long id);
}
