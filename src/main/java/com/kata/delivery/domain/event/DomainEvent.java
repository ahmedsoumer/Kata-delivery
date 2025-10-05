package com.kata.delivery.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base interface for all domain events (DDD pattern)
 */
public interface DomainEvent {
    UUID getEventId();
    LocalDateTime getOccurredOn();
    String getEventType();
}
