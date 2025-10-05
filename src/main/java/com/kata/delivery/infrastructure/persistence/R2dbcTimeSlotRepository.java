package com.kata.delivery.infrastructure.persistence;

import com.kata.delivery.domain.DeliveryMode;
import com.kata.delivery.domain.model.TimeSlotAggregate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

/**
 * R2DBC repository for TimeSlotAggregate
 * Infrastructure adapter implementing persistence
 */
@Repository
public interface R2dbcTimeSlotRepository extends R2dbcRepository<TimeSlotAggregate, Long> {
    
    Flux<TimeSlotAggregate> findByDeliveryMode(DeliveryMode deliveryMode);
    
    Flux<TimeSlotAggregate> findByDate(LocalDate date);
    
    Flux<TimeSlotAggregate> findByDeliveryModeAndDate(DeliveryMode deliveryMode, LocalDate date);
    
    @Query("SELECT * FROM time_slots WHERE delivery_mode = :deliveryMode AND date = :date AND current_reservations < capacity")
    Flux<TimeSlotAggregate> findAvailableSlots(DeliveryMode deliveryMode, LocalDate date);
}
