package com.kata.delivery.infrastructure.persistence;

import com.kata.delivery.domain.DeliveryMode;
import com.kata.delivery.domain.model.TimeSlotAggregate;
import com.kata.delivery.domain.port.outbound.TimeSlotRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Adapter implementing TimeSlotRepositoryPort using R2DBC
 * Part of hexagonal architecture - infrastructure adapter
 */
@Component
@RequiredArgsConstructor
public class TimeSlotRepositoryAdapter implements TimeSlotRepositoryPort {

    private final R2dbcTimeSlotRepository repository;

    @Override
    public Mono<TimeSlotAggregate> save(TimeSlotAggregate timeSlot) {
        return repository.save(timeSlot);
    }

    @Override
    public Mono<TimeSlotAggregate> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Flux<TimeSlotAggregate> findAll() {
        return repository.findAll();
    }

    @Override
    public Flux<TimeSlotAggregate> findByDeliveryMode(DeliveryMode deliveryMode) {
        return repository.findByDeliveryMode(deliveryMode);
    }

    @Override
    public Flux<TimeSlotAggregate> findByDate(LocalDate date) {
        return repository.findByDate(date);
    }

    @Override
    public Flux<TimeSlotAggregate> findByDeliveryModeAndDate(DeliveryMode deliveryMode, LocalDate date) {
        return repository.findByDeliveryModeAndDate(deliveryMode, date);
    }

    @Override
    public Flux<TimeSlotAggregate> findAvailableSlots(DeliveryMode deliveryMode, LocalDate date) {
        return repository.findAvailableSlots(deliveryMode, date);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }
}
