package com.kata.delivery.domain.event;

import com.kata.delivery.domain.DeliveryMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Domain event published when a reservation is created
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationCreatedEvent implements DomainEvent {
    
    @Builder.Default
    private UUID eventId = UUID.randomUUID();
    
    @Builder.Default
    private LocalDateTime occurredOn = LocalDateTime.now();
    
    private Long reservationId;
    private String customerName;
    private String customerEmail;
    private Long timeSlotId;
    private DeliveryMode deliveryMode;
    private LocalDate deliveryDate;
    private LocalTime deliveryStartTime;
    private LocalTime deliveryEndTime;
    
    @Override
    public String getEventType() {
        return "ReservationCreated";
    }
}
