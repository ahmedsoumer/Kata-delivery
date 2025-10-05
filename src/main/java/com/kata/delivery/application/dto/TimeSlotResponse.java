package com.kata.delivery.application.dto;

import com.kata.delivery.domain.DeliveryMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Response DTO for TimeSlot information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotResponse {
    private Long id;
    private DeliveryMode deliveryMode;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer capacity;
    private Integer currentReservations;
    private Integer availableSlots;
    private Boolean isAvailable;
}
