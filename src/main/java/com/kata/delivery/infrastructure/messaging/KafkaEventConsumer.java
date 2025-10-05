package com.kata.delivery.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.delivery.domain.event.ReservationCancelledEvent;
import com.kata.delivery.domain.event.ReservationCreatedEvent;
import com.kata.delivery.domain.event.TimeSlotCapacityChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka event consumer listening to domain events
 * Demonstrates event-driven architecture with event handlers
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {

    private final ObjectMapper objectMapper;

    /**
     * Handle ReservationCreated events
     * This could trigger notifications, analytics, etc.
     */
    @KafkaListener(topics = "delivery.events.reservationcreated", groupId = "delivery-service")
    public void handleReservationCreated(String eventJson) {
        try {
            ReservationCreatedEvent event = objectMapper.readValue(eventJson, ReservationCreatedEvent.class);
            log.info("📧 Event received: Reservation created for customer: {} on {} at {}", 
                    event.getCustomerEmail(), 
                    event.getDeliveryDate(), 
                    event.getDeliveryStartTime());
            
            // Here you could:
            // - Send confirmation email
            // - Update analytics
            // - Trigger external systems
            // - Create audit logs
            
        } catch (Exception e) {
            log.error("Error handling ReservationCreated event", e);
        }
    }

    /**
     * Handle ReservationCancelled events
     */
    @KafkaListener(topics = "delivery.events.reservationcancelled", groupId = "delivery-service")
    public void handleReservationCancelled(String eventJson) {
        try {
            ReservationCancelledEvent event = objectMapper.readValue(eventJson, ReservationCancelledEvent.class);
            log.info("❌ Event received: Reservation {} cancelled for customer: {}", 
                    event.getReservationId(), 
                    event.getCustomerEmail());
            
            // Here you could:
            // - Send cancellation confirmation
            // - Update customer profile
            // - Trigger refund process
            
        } catch (Exception e) {
            log.error("Error handling ReservationCancelled event", e);
        }
    }

    /**
     * Handle TimeSlotCapacityChanged events
     */
    @KafkaListener(topics = "delivery.events.timeslotcapacitychanged", groupId = "delivery-service")
    public void handleTimeSlotCapacityChanged(String eventJson) {
        try {
            TimeSlotCapacityChangedEvent event = objectMapper.readValue(eventJson, TimeSlotCapacityChangedEvent.class);
            
            if (Boolean.TRUE.equals(event.getIsFullyBooked())) {
                log.warn("⚠️ Event received: TimeSlot {} for {} is now fully booked", 
                        event.getTimeSlotId(), 
                        event.getDeliveryMode());
                
                // Here you could:
                // - Trigger alerts
                // - Update cache
                // - Notify admin dashboard
            } else {
                log.info("✅ Event received: TimeSlot {} capacity changed: {}/{}", 
                        event.getTimeSlotId(), 
                        event.getCurrentReservations(), 
                        event.getCapacity());
            }
            
        } catch (Exception e) {
            log.error("Error handling TimeSlotCapacityChanged event", e);
        }
    }
}
