package com.kata.delivery.infrastructure.persistence;

import com.kata.delivery.domain.ReservationStatus;
import com.kata.delivery.domain.model.ReservationAggregate;
import com.kata.delivery.domain.port.outbound.ReservationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter implementing ReservationRepositoryPort using R2DBC
 * Part of hexagonal architecture - infrastructure adapter
 */
@Component
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepositoryPort {

    private final R2dbcReservationRepository repository;

    @Override
    public Mono<ReservationAggregate> save(ReservationAggregate reservation) {
        return repository.save(reservation);
    }

    @Override
    public Mono<ReservationAggregate> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Flux<ReservationAggregate> findAll() {
        return repository.findAll();
    }

    @Override
    public Flux<ReservationAggregate> findByCustomerEmail(String email) {
        return repository.findByCustomerEmail(email);
    }

    @Override
    public Flux<ReservationAggregate> findByStatus(ReservationStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public Flux<ReservationAggregate> findByTimeSlotId(Long timeSlotId) {
        return repository.findByTimeSlotId(timeSlotId);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}
