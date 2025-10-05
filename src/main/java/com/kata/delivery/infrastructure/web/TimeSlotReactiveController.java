package com.kata.delivery.infrastructure.web;

import com.kata.delivery.application.dto.TimeSlotResponse;
import com.kata.delivery.domain.DeliveryMode;
import com.kata.delivery.domain.port.inbound.TimeSlotUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * Reactive REST controller for time slot operations (WebFlux)
 * Inbound adapter in hexagonal architecture
 */
@RestController
@RequestMapping("/api/v2/time-slots")
@RequiredArgsConstructor
@Tag(name = "Time Slots (Reactive)", description = "Reactive API for managing delivery time slots")
public class TimeSlotReactiveController {

    private final TimeSlotUseCase timeSlotUseCase;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all time slots (reactive)", 
               description = "Retrieve all available time slots using reactive streams")
    public Flux<TimeSlotResponse> getAllTimeSlots() {
        return timeSlotUseCase.getAllTimeSlots();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get time slot by ID (reactive)", 
               description = "Retrieve a specific time slot by its ID")
    public Mono<TimeSlotResponse> getTimeSlotById(
            @Parameter(description = "Time slot ID") @PathVariable Long id) {
        return timeSlotUseCase.getTimeSlotById(id);
    }

    @GetMapping(value = "/by-delivery-mode/{deliveryMode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get time slots by delivery mode (reactive)", 
               description = "Retrieve all time slots for a specific delivery mode")
    public Flux<TimeSlotResponse> getTimeSlotsByDeliveryMode(
            @Parameter(description = "Delivery mode (DRIVE, DELIVERY, DELIVERY_TODAY, DELIVERY_ASAP)") 
            @PathVariable DeliveryMode deliveryMode) {
        return timeSlotUseCase.getTimeSlotsByDeliveryMode(deliveryMode);
    }

    @GetMapping(value = "/by-date", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get time slots by date (reactive)", 
               description = "Retrieve all time slots for a specific date")
    public Flux<TimeSlotResponse> getTimeSlotsByDate(
            @Parameter(description = "Date in format yyyy-MM-dd") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return timeSlotUseCase.getTimeSlotsByDate(date);
    }

    @GetMapping(value = "/available", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get available time slots (reactive)", 
               description = "Retrieve available time slots for a specific delivery mode and date using reactive streams")
    public Flux<TimeSlotResponse> getAvailableTimeSlots(
            @Parameter(description = "Delivery mode") 
            @RequestParam DeliveryMode deliveryMode,
            @Parameter(description = "Date in format yyyy-MM-dd") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return timeSlotUseCase.getAvailableTimeSlots(deliveryMode, date);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream time slots (SSE)", 
               description = "Stream time slots using Server-Sent Events")
    public Flux<TimeSlotResponse> streamTimeSlots() {
        return timeSlotUseCase.getAllTimeSlots();
    }
}
