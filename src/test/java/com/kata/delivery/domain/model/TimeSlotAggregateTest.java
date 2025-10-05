package com.kata.delivery.domain.model;

import com.kata.delivery.domain.DeliveryMode;
import com.kata.delivery.domain.event.TimeSlotCapacityChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TimeSlotAggregate (DDD pattern testing)
 */
class TimeSlotAggregateTest {

    private TimeSlotAggregate timeSlot;

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
    }

    @Test
    void constructor_WithValidData_CreatesTimeSlot() {
        assertNotNull(timeSlot);
        assertEquals(DeliveryMode.DELIVERY, timeSlot.getDeliveryMode());
        assertEquals(5, timeSlot.getCapacity());
        assertEquals(0, timeSlot.getCurrentReservations());
        assertTrue(timeSlot.hasAvailableCapacity());
    }

    @Test
    void constructor_WithInvalidCapacity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new TimeSlotAggregate(
                        DeliveryMode.DELIVERY,
                        LocalDate.now(),
                        LocalTime.of(10, 0),
                        LocalTime.of(12, 0),
                        0
                )
        );
    }

    @Test
    void constructor_WithInvalidTimeRange_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new TimeSlotAggregate(
                        DeliveryMode.DELIVERY,
                        LocalDate.now(),
                        LocalTime.of(12, 0),
                        LocalTime.of(10, 0),
                        5
                )
        );
    }

    @Test
    void reserveSlot_WithAvailableCapacity_IncrementsReservations() {
        assertEquals(0, timeSlot.getCurrentReservations());
        
        timeSlot.reserveSlot();
        
        assertEquals(1, timeSlot.getCurrentReservations());
        assertTrue(timeSlot.hasAvailableCapacity());
    }

    @Test
    void reserveSlot_PublishesDomainEvent() {
        timeSlot.reserveSlot();
        
        var events = timeSlot.pullDomainEvents();
        
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof TimeSlotCapacityChangedEvent);
        
        TimeSlotCapacityChangedEvent event = (TimeSlotCapacityChangedEvent) events.get(0);
        assertEquals(1, event.getCurrentReservations());
        assertEquals(0, event.getPreviousReservations());
        assertFalse(event.getIsFullyBooked());
    }

    @Test
    void reserveSlot_WhenFull_ThrowsException() {
        // Fill up the time slot
        for (int i = 0; i < 5; i++) {
            timeSlot.reserveSlot();
        }
        
        assertFalse(timeSlot.hasAvailableCapacity());
        assertThrows(IllegalStateException.class, () -> timeSlot.reserveSlot());
    }

    @Test
    void releaseSlot_DecrementsReservations() {
        timeSlot.reserveSlot();
        timeSlot.pullDomainEvents(); // Clear events
        
        timeSlot.releaseSlot();
        
        assertEquals(0, timeSlot.getCurrentReservations());
        assertTrue(timeSlot.hasAvailableCapacity());
    }

    @Test
    void releaseSlot_PublishesDomainEvent() {
        timeSlot.reserveSlot();
        timeSlot.pullDomainEvents(); // Clear events
        
        timeSlot.releaseSlot();
        
        var events = timeSlot.pullDomainEvents();
        
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof TimeSlotCapacityChangedEvent);
        
        TimeSlotCapacityChangedEvent event = (TimeSlotCapacityChangedEvent) events.get(0);
        assertEquals(0, event.getCurrentReservations());
        assertEquals(1, event.getPreviousReservations());
    }

    @Test
    void pullDomainEvents_ClearsEvents() {
        timeSlot.reserveSlot();
        
        var events1 = timeSlot.pullDomainEvents();
        assertEquals(1, events1.size());
        
        var events2 = timeSlot.pullDomainEvents();
        assertEquals(0, events2.size());
    }
}
