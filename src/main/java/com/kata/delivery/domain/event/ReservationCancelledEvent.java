package com.kata.delivery.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a reservation is cancelled
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationCancelledEvent implements DomainEvent {
    
    @Builder.Default
    private UUID eventId = UUID.randomUUID();
    
    @Builder.Default
    private LocalDateTime occurredOn = LocalDateTime.now();
    
    private Long reservationId;
    private String customerEmail;
    private Long timeSlotId;
    private String cancellationReason;
    
    @Override
    public String getEventType() {
        return "ReservationCancelled";
    }
}
