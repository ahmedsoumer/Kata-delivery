package com.kata.delivery.domain.model;

import com.kata.delivery.domain.DeliveryMode;
import com.kata.delivery.domain.event.DomainEvent;
import com.kata.delivery.domain.event.TimeSlotCapacityChangedEvent;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TimeSlot Aggregate Root (DDD pattern)
 * Manages its own consistency and publishes domain events
 */
@Table("time_slots")
@Getter
public class TimeSlotAggregate {

    @Id
    private Long id;
    
    private DeliveryMode deliveryMode;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer capacity;
    private Integer currentReservations;
    
    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    // For R2DBC
    public TimeSlotAggregate() {
        this.currentReservations = 0;
    }
    
    public TimeSlotAggregate(DeliveryMode deliveryMode, LocalDate date, 
                            LocalTime startTime, LocalTime endTime, Integer capacity) {
        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        this.deliveryMode = deliveryMode;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.currentReservations = 0;
    }
    
    /**
     * Business logic: Reserve a slot
     */
    public void reserveSlot() {
        if (!hasAvailableCapacity()) {
            throw new IllegalStateException("Time slot is fully booked");
        }
        
        Integer previous = this.currentReservations;
        this.currentReservations++;
        
        // Publish domain event
        domainEvents.add(TimeSlotCapacityChangedEvent.builder()
                .timeSlotId(this.id)
                .deliveryMode(this.deliveryMode)
                .previousReservations(previous)
                .currentReservations(this.currentReservations)
                .capacity(this.capacity)
                .isFullyBooked(!hasAvailableCapacity())
                .build());
    }
    
    /**
     * Business logic: Release a slot
     */
    public void releaseSlot() {
        if (this.currentReservations > 0) {
            Integer previous = this.currentReservations;
            this.currentReservations--;
            
            // Publish domain event
            domainEvents.add(TimeSlotCapacityChangedEvent.builder()
                    .timeSlotId(this.id)
                    .deliveryMode(this.deliveryMode)
                    .previousReservations(previous)
                    .currentReservations(this.currentReservations)
                    .capacity(this.capacity)
                    .isFullyBooked(false)
                    .build());
        }
    }
    
    /**
     * Check if slot has available capacity
     */
    public boolean hasAvailableCapacity() {
        return currentReservations < capacity;
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
    
    public void setDeliveryMode(DeliveryMode deliveryMode) {
        this.deliveryMode = deliveryMode;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public void setCurrentReservations(Integer currentReservations) {
        this.currentReservations = currentReservations;
    }
}
