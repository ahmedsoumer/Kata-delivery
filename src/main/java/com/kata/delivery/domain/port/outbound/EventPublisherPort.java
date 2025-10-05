package com.kata.delivery.domain.port.outbound;

import com.kata.delivery.domain.event.DomainEvent;
import reactor.core.publisher.Mono;

/**
 * Outbound port for publishing domain events
 * Part of hexagonal architecture - domain defines the contract
 */
public interface EventPublisherPort {
    
    /**
     * Publish a domain event to the event bus (Kafka)
     */
    Mono<Void> publish(DomainEvent event);
}
