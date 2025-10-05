package com.kata.delivery.infrastructure.config;

import com.kata.delivery.domain.DeliveryMode;
import com.kata.delivery.domain.model.TimeSlotAggregate;
import com.kata.delivery.domain.port.outbound.TimeSlotRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Initialize sample data for reactive application
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReactiveDataInitializer implements CommandLineRunner {

    private final TimeSlotRepositoryPort timeSlotRepository;

    @Override
    public void run(String... args) {
        log.info("🚀 Initializing sample time slots (Reactive)...");
        
        List<TimeSlotAggregate> timeSlots = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // DRIVE time slots
        timeSlots.add(createTimeSlot(DeliveryMode.DRIVE, today.plusDays(1), 
                LocalTime.of(9, 0), LocalTime.of(10, 0), 5));
        timeSlots.add(createTimeSlot(DeliveryMode.DRIVE, today.plusDays(1), 
                LocalTime.of(10, 0), LocalTime.of(11, 0), 5));
        timeSlots.add(createTimeSlot(DeliveryMode.DRIVE, today.plusDays(1), 
                LocalTime.of(14, 0), LocalTime.of(15, 0), 5));
        timeSlots.add(createTimeSlot(DeliveryMode.DRIVE, today.plusDays(2), 
                LocalTime.of(9, 0), LocalTime.of(10, 0), 5));
        
        // DELIVERY time slots
        timeSlots.add(createTimeSlot(DeliveryMode.DELIVERY, today.plusDays(2), 
                LocalTime.of(8, 0), LocalTime.of(12, 0), 10));
        timeSlots.add(createTimeSlot(DeliveryMode.DELIVERY, today.plusDays(2), 
                LocalTime.of(14, 0), LocalTime.of(18, 0), 10));
        timeSlots.add(createTimeSlot(DeliveryMode.DELIVERY, today.plusDays(3), 
                LocalTime.of(8, 0), LocalTime.of(12, 0), 10));
        timeSlots.add(createTimeSlot(DeliveryMode.DELIVERY, today.plusDays(3), 
                LocalTime.of(14, 0), LocalTime.of(18, 0), 10));
        
        // DELIVERY_TODAY time slots
        timeSlots.add(createTimeSlot(DeliveryMode.DELIVERY_TODAY, today, 
                LocalTime.of(15, 0), LocalTime.of(18, 0), 3));
        timeSlots.add(createTimeSlot(DeliveryMode.DELIVERY_TODAY, today, 
                LocalTime.of(18, 0), LocalTime.of(21, 0), 3));
        
        // DELIVERY_ASAP time slots
        timeSlots.add(createTimeSlot(DeliveryMode.DELIVERY_ASAP, today, 
                LocalTime.now().plusHours(1), LocalTime.now().plusHours(2), 2));
        timeSlots.add(createTimeSlot(DeliveryMode.DELIVERY_ASAP, today, 
                LocalTime.now().plusHours(2), LocalTime.now().plusHours(3), 2));
        
        // Save all time slots reactively
        Flux.fromIterable(timeSlots)
                .flatMap(timeSlotRepository::save)
                .doOnNext(ts -> log.debug("Saved time slot: {} for {} on {}", 
                        ts.getId(), ts.getDeliveryMode(), ts.getDate()))
                .doOnComplete(() -> log.info("✅ Successfully initialized {} time slots", timeSlots.size()))
                .subscribe();
    }
    
    private TimeSlotAggregate createTimeSlot(DeliveryMode mode, LocalDate date, 
                                             LocalTime start, LocalTime end, int capacity) {
        return new TimeSlotAggregate(mode, date, start, end, capacity);
    }
}
