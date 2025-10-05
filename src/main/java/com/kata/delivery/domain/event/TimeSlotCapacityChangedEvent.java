package com.kata.delivery.domain.event;

import com.kata.delivery.domain.DeliveryMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a time slot capacity changes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotCapacityChangedEvent implements DomainEvent {
    
    @Builder.Default
    private UUID eventId = UUID.randomUUID();
    
    @Builder.Default
    private LocalDateTime occurredOn = LocalDateTime.now();
    
    private Long timeSlotId;
    private DeliveryMode deliveryMode;
    private Integer previousReservations;
    private Integer currentReservations;
    private Integer capacity;
    private Boolean isFullyBooked;
    
    @Override
    public String getEventType() {
        return "TimeSlotCapacityChanged";
    }
}
