package com.kata.delivery.domain.port.outbound;

import com.kata.delivery.domain.DeliveryMode;
import com.kata.delivery.domain.model.TimeSlotAggregate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Outbound port (interface) for TimeSlot persistence
 * Part of hexagonal architecture - domain defines the contract
 */
public interface TimeSlotRepositoryPort {
    
    Mono<TimeSlotAggregate> save(TimeSlotAggregate timeSlot);
    
    Mono<TimeSlotAggregate> findById(Long id);
    
    Flux<TimeSlotAggregate> findAll();
    
    Flux<TimeSlotAggregate> findByDeliveryMode(DeliveryMode deliveryMode);
    
    Flux<TimeSlotAggregate> findByDate(LocalDate date);
    
    Flux<TimeSlotAggregate> findByDeliveryModeAndDate(DeliveryMode deliveryMode, LocalDate date);
    
    Flux<TimeSlotAggregate> findAvailableSlots(DeliveryMode deliveryMode, LocalDate date);
    
    Mono<Void> deleteById(Long id);
}
