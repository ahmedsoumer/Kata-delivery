package com.kata.delivery.application.service;

import com.kata.delivery.application.dto.CreateReservationCommand;
import com.kata.delivery.application.dto.ReservationResponse;
import com.kata.delivery.application.dto.TimeSlotResponse;
import com.kata.delivery.domain.event.DomainEvent;
import com.kata.delivery.domain.model.CustomerInfo;
import com.kata.delivery.domain.model.ReservationAggregate;
import com.kata.delivery.domain.model.TimeSlotAggregate;
import com.kata.delivery.domain.port.inbound.ReservationUseCase;
import com.kata.delivery.domain.port.outbound.EventPublisherPort;
import com.kata.delivery.domain.port.outbound.ReservationRepositoryPort;
import com.kata.delivery.domain.port.outbound.TimeSlotRepositoryPort;
import com.kata.delivery.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Application service implementing reservation use cases
 * Orchestrates domain logic and infrastructure concerns
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationApplicationService implements ReservationUseCase {

    private final ReservationRepositoryPort reservationRepository;
    private final TimeSlotRepositoryPort timeSlotRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public Mono<ReservationResponse> createReservation(CreateReservationCommand command) {
        log.info("Creating reservation for customer: {}", command.getCustomerEmail());
        
        CustomerInfo customerInfo = new CustomerInfo(
                command.getCustomerName(), 
                command.getCustomerEmail()
        );
        
        return timeSlotRepository.findById(command.getTimeSlotId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("TimeSlot", command.getTimeSlotId())))
                .flatMap(timeSlot -> {
                    // Create reservation using domain logic
                    ReservationAggregate reservation = ReservationAggregate.createReservation(
                            customerInfo, timeSlot
                    );
                    
                    // Reserve slot in aggregate
                    timeSlot.reserveSlot();
                    
                    // Save both aggregates
                    return timeSlotRepository.save(timeSlot)
                            .then(reservationRepository.save(reservation))
                            .flatMap(savedReservation -> {
                                // Publish all domain events
                                return publishEvents(reservation, timeSlot)
                                        .then(timeSlotRepository.findById(timeSlot.getId()))
                                        .map(ts -> toResponse(savedReservation, ts));
                            });
                });
    }

    @Override
    public Mono<ReservationResponse> getReservationById(Long id) {
        log.debug("Fetching reservation with id: {}", id);
        
        return reservationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Reservation", id)))
                .flatMap(reservation -> 
                    timeSlotRepository.findById(reservation.getTimeSlotId())
                            .map(timeSlot -> toResponse(reservation, timeSlot))
                );
    }

    @Override
    public Flux<ReservationResponse> getAllReservations() {
        log.debug("Fetching all reservations");
        
        return reservationRepository.findAll()
                .flatMap(reservation ->
                    timeSlotRepository.findById(reservation.getTimeSlotId())
                            .map(timeSlot -> toResponse(reservation, timeSlot))
                );
    }

    @Override
    public Flux<ReservationResponse> getReservationsByCustomer(String email) {
        log.debug("Fetching reservations for customer: {}", email);
        
        return reservationRepository.findByCustomerEmail(email)
                .flatMap(reservation ->
                    timeSlotRepository.findById(reservation.getTimeSlotId())
                            .map(timeSlot -> toResponse(reservation, timeSlot))
                );
    }

    @Override
    @Transactional
    public Mono<ReservationResponse> cancelReservation(Long id, String reason) {
        log.info("Cancelling reservation: {}", id);
        
        return reservationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Reservation", id)))
                .flatMap(reservation -> {
                    // Cancel using domain logic
                    reservation.cancel(reason != null ? reason : "Customer requested");
                    
                    return timeSlotRepository.findById(reservation.getTimeSlotId())
                            .flatMap(timeSlot -> {
                                // Release slot
                                timeSlot.releaseSlot();
                                
                                // Save both aggregates
                                return timeSlotRepository.save(timeSlot)
                                        .then(reservationRepository.save(reservation))
                                        .flatMap(savedReservation -> {
                                            // Publish events
                                            return publishEvents(reservation, timeSlot)
                                                    .then(timeSlotRepository.findById(timeSlot.getId()))
                                                    .map(ts -> toResponse(savedReservation, ts));
                                        });
                            });
                });
    }
    
    private Mono<Void> publishEvents(ReservationAggregate reservation, TimeSlotAggregate timeSlot) {
        Flux<DomainEvent> allEvents = Flux.concat(
                Flux.fromIterable(reservation.pullDomainEvents()),
                Flux.fromIterable(timeSlot.pullDomainEvents())
        );
        
        return allEvents
                .flatMap(eventPublisher::publish)
                .then();
    }
    
    private ReservationResponse toResponse(ReservationAggregate reservation, TimeSlotAggregate timeSlot) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .customerName(reservation.getCustomerName())
                .customerEmail(reservation.getCustomerEmail())
                .timeSlotId(reservation.getTimeSlotId())
                .timeSlot(toTimeSlotResponse(timeSlot))
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .cancelledAt(reservation.getCancelledAt())
                .build();
    }
    
    private TimeSlotResponse toTimeSlotResponse(TimeSlotAggregate timeSlot) {
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
