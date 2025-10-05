package com.kata.delivery.application.service;

import com.kata.delivery.application.dto.TimeSlotResponse;
import com.kata.delivery.domain.DeliveryMode;
import com.kata.delivery.domain.model.TimeSlotAggregate;
import com.kata.delivery.domain.port.inbound.TimeSlotUseCase;
import com.kata.delivery.domain.port.outbound.TimeSlotRepositoryPort;
import com.kata.delivery.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Application service implementing time slot use cases
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TimeSlotApplicationService implements TimeSlotUseCase {

    private final TimeSlotRepositoryPort timeSlotRepository;

    @Override
    public Flux<TimeSlotResponse> getAllTimeSlots() {
        log.debug("Fetching all time slots");
        return timeSlotRepository.findAll()
                .map(this::toResponse);
    }

    @Override
    public Mono<TimeSlotResponse> getTimeSlotById(Long id) {
        log.debug("Fetching time slot with id: {}", id);
        return timeSlotRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("TimeSlot", id)))
                .map(this::toResponse);
    }

    @Override
    public Flux<TimeSlotResponse> getTimeSlotsByDeliveryMode(DeliveryMode deliveryMode) {
        log.debug("Fetching time slots for delivery mode: {}", deliveryMode);
        return timeSlotRepository.findByDeliveryMode(deliveryMode)
                .map(this::toResponse);
    }

    @Override
    public Flux<TimeSlotResponse> getTimeSlotsByDate(LocalDate date) {
        log.debug("Fetching time slots for date: {}", date);
        return timeSlotRepository.findByDate(date)
                .map(this::toResponse);
    }

    @Override
    public Flux<TimeSlotResponse> getAvailableTimeSlots(DeliveryMode deliveryMode, LocalDate date) {
        log.debug("Fetching available time slots for mode: {} and date: {}", deliveryMode, date);
        return timeSlotRepository.findAvailableSlots(deliveryMode, date)
                .map(this::toResponse);
    }
    
    private TimeSlotResponse toResponse(TimeSlotAggregate timeSlot) {
        return TimeSlotResponse.builder()
                .id(timeSlot.getId())
                .deliveryMode(timeSlot.getDeliveryMode())
                .date(timeSlot.getDate())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .capacity(timeSlot.getCapacity())
                .currentReservations(timeSlot.getCurrentReservations())
                .availableSlots(timeSlot.getCapacity() - timeSlot.getCurrentReservations())
                .isAvailable(timeSlot.hasAvailableCapacity())
                .build();
    }
}
