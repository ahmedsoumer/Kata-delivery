package com.kata.delivery.infrastructure.persistence;

import com.kata.delivery.domain.ReservationStatus;
import com.kata.delivery.domain.model.ReservationAggregate;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * R2DBC repository for ReservationAggregate
 * Infrastructure adapter implementing persistence
 */
@Repository
public interface R2dbcReservationRepository extends R2dbcRepository<ReservationAggregate, Long> {
    
    Flux<ReservationAggregate> findByCustomerEmail(String email);
    
    Flux<ReservationAggregate> findByStatus(ReservationStatus status);
    
    Flux<ReservationAggregate> findByTimeSlotId(Long timeSlotId);
}
