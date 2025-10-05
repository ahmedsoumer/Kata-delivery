package com.kata.delivery.domain.model;

import com.kata.delivery.domain.ReservationStatus;
import com.kata.delivery.domain.event.DomainEvent;
import com.kata.delivery.domain.event.ReservationCancelledEvent;
import com.kata.delivery.domain.event.ReservationCreatedEvent;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reservation Aggregate Root (DDD pattern)
 * Encapsulates reservation business logic and invariants
 */
@Table("reservations")
@Getter
public class ReservationAggregate {

    @Id
    private Long id;
    
    private String customerName;
    private String customerEmail;
    private Long timeSlotId;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;
    
    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    // For R2DBC
    public ReservationAggregate() {
    }
    
    /**
     * Factory method to create a new reservation
     */
    public static ReservationAggregate createReservation(
            CustomerInfo customerInfo, 
            TimeSlotAggregate timeSlot) {
        
        if (!timeSlot.hasAvailableCapacity()) {
            throw new IllegalStateException("Time slot is fully booked");
        }
        
        ReservationAggregate reservation = new ReservationAggregate();
        reservation.customerName = customerInfo.getName();
        reservation.customerEmail = customerInfo.getEmail();
        reservation.timeSlotId = timeSlot.getId();
        reservation.status = ReservationStatus.CONFIRMED;
        reservation.createdAt = LocalDateTime.now();
        
        // Publish domain event
        reservation.domainEvents.add(ReservationCreatedEvent.builder()
                .reservationId(reservation.id)
                .customerName(reservation.customerName)
                .customerEmail(reservation.customerEmail)
                .timeSlotId(timeSlot.getId())
                .deliveryMode(timeSlot.getDeliveryMode())
                .deliveryDate(timeSlot.getDate())
                .deliveryStartTime(timeSlot.getStartTime())
                .deliveryEndTime(timeSlot.getEndTime())
                .build());
        
        return reservation;
    }
    
    /**
     * Business logic: Cancel reservation
     */
    public void cancel(String reason) {
        if (this.status == ReservationStatus.CANCELLED) {
            return; // Already cancelled, idempotent
        }
        
        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        
        // Publish domain event
        domainEvents.add(ReservationCancelledEvent.builder()
                .reservationId(this.id)
                .customerEmail(this.customerEmail)
                .timeSlotId(this.timeSlotId)
                .cancellationReason(reason)
                .build());
    }
    
    /**
     * Check if reservation is active
     */
    public boolean isActive() {
        return status == ReservationStatus.CONFIRMED;
    }
    
    /**
     * Get and clear domain events (for event publishing)
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }
    
    // Setters for R2DBC
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public void setTimeSlotId(Long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
