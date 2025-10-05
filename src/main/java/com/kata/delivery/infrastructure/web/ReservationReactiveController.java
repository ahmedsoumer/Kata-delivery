package com.kata.delivery.infrastructure.web;

import com.kata.delivery.application.dto.CreateReservationCommand;
import com.kata.delivery.application.dto.ReservationResponse;
import com.kata.delivery.domain.port.inbound.ReservationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive REST controller for reservation operations (WebFlux)
 * Inbound adapter in hexagonal architecture
 */
@RestController
@RequestMapping("/api/v2/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations (Reactive)", description = "Reactive API for managing delivery reservations")
public class ReservationReactiveController {

    private final ReservationUseCase reservationUseCase;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a reservation (reactive)", 
               description = "Create a new reservation for a customer on a specific time slot using reactive approach")
    public Mono<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationCommand command) {
        return reservationUseCase.createReservation(command);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all reservations (reactive)", 
               description = "Retrieve all reservations using reactive streams")
    public Flux<ReservationResponse> getAllReservations() {
        return reservationUseCase.getAllReservations();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get reservation by ID (reactive)", 
               description = "Retrieve a specific reservation by its ID")
    public Mono<ReservationResponse> getReservationById(
            @Parameter(description = "Reservation ID") @PathVariable Long id) {
        return reservationUseCase.getReservationById(id);
    }

    @GetMapping(value = "/by-customer", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get reservations by customer email (reactive)", 
               description = "Retrieve all reservations for a specific customer")
    public Flux<ReservationResponse> getReservationsByCustomerEmail(
            @Parameter(description = "Customer email address") @RequestParam String email) {
        return reservationUseCase.getReservationsByCustomer(email);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cancel a reservation (reactive)", 
               description = "Cancel an existing reservation")
    public Mono<ReservationResponse> cancelReservation(
            @Parameter(description = "Reservation ID") @PathVariable Long id,
            @Parameter(description = "Cancellation reason") @RequestParam(required = false) String reason) {
        return reservationUseCase.cancelReservation(id, reason);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream reservations (SSE)", 
               description = "Stream reservations using Server-Sent Events")
    public Flux<ReservationResponse> streamReservations() {
        return reservationUseCase.getAllReservations();
    }
}
