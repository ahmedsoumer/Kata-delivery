package com.kata.delivery.application.service;

import com.kata.delivery.application.dto.CreateReservationCommand;
import com.kata.delivery.domain.DeliveryMode;
import com.kata.delivery.domain.ReservationStatus;
import com.kata.delivery.domain.model.ReservationAggregate;
import com.kata.delivery.domain.model.TimeSlotAggregate;
import com.kata.delivery.domain.port.outbound.EventPublisherPort;
import com.kata.delivery.domain.port.outbound.ReservationRepositoryPort;
import com.kata.delivery.domain.port.outbound.TimeSlotRepositoryPort;
import com.kata.delivery.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Reactive tests for ReservationApplicationService
 */
@ExtendWith(MockitoExtension.class)
class ReservationApplicationServiceTest {

    @Mock
    private ReservationRepositoryPort reservationRepository;

    @Mock
    private TimeSlotRepositoryPort timeSlotRepository;

    @Mock
    private EventPublisherPort eventPublisher;

    @InjectMocks
    private ReservationApplicationService reservationService;

    private TimeSlotAggregate timeSlot;
    private CreateReservationCommand command;

    @BeforeEach
    void setUp() {
        timeSlot = new TimeSlotAggregate(
                DeliveryMode.DELIVERY,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                5
        );
        timeSlot.setId(1L);

        command = CreateReservationCommand.builder()
                .customerName("Ahmed Soumer")
                .customerEmail("ahmed.soumer@example.com")
                .timeSlotId(1L)
                .build();
    }

    @Test
    void createReservation_Success() {
        // Given
        when(timeSlotRepository.findById(1L)).thenReturn(Mono.just(timeSlot));
        when(timeSlotRepository.save(any())).thenReturn(Mono.just(timeSlot));
        when(reservationRepository.save(any())).thenAnswer(invocation -> {
            ReservationAggregate res = invocation.getArgument(0);
            res.setId(1L);
            return Mono.just(res);
        });
        when(eventPublisher.publish(any())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(reservationService.createReservation(command))
                .expectNextMatches(response ->
                        response.getId() != null &&
                        response.getCustomerEmail().equals("ahmed.soumer@example.com") &&
                        response.getStatus() == ReservationStatus.CONFIRMED
                )
                .verifyComplete();

        verify(timeSlotRepository).save(any());
        verify(reservationRepository).save(any());
    }

    @Test
    void createReservation_TimeSlotNotFound() {
        // Given
        when(timeSlotRepository.findById(1L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(reservationService.createReservation(command))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void cancelReservation_Success() {
        // Given
        ReservationAggregate reservation = ReservationAggregate.createReservation(
                new com.kata.delivery.domain.model.CustomerInfo("Ahmed Soumer", "ahmed.soumer@example.com"),
                timeSlot
        );
        reservation.setId(1L);
        timeSlot.reserveSlot();

        when(reservationRepository.findById(1L)).thenReturn(Mono.just(reservation));
        when(timeSlotRepository.findById(1L)).thenReturn(Mono.just(timeSlot));
        when(timeSlotRepository.save(any())).thenReturn(Mono.just(timeSlot));
        when(reservationRepository.save(any())).thenReturn(Mono.just(reservation));
        when(eventPublisher.publish(any())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(reservationService.cancelReservation(1L, "Customer requested"))
                .expectNextMatches(response ->
                        response.getStatus() == ReservationStatus.CANCELLED
                )
                .verifyComplete();
    }

    @Test
    void getAllReservations_ReturnsFlux() {
        // Given
        ReservationAggregate res1 = ReservationAggregate.createReservation(
                new com.kata.delivery.domain.model.CustomerInfo("Ahmed Soumer", "ahmed@example.com"),
                timeSlot
        );
        res1.setId(1L);

        when(reservationRepository.findAll()).thenReturn(Flux.just(res1));
        when(timeSlotRepository.findById(any())).thenReturn(Mono.just(timeSlot));

        // When & Then
        StepVerifier.create(reservationService.getAllReservations())
                .expectNextCount(1)
                .verifyComplete();
    }
}
