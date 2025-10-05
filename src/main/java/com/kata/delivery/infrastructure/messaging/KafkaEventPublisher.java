package com.kata.delivery.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.delivery.domain.event.DomainEvent;
import com.kata.delivery.domain.port.outbound.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Kafka adapter implementing EventPublisherPort
 * Publishes domain events to Kafka topics
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher implements EventPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String TOPIC_PREFIX = "delivery.events.";

    @Override
    public Mono<Void> publish(DomainEvent event) {
        return Mono.fromRunnable(() -> {
            try {
                String topic = TOPIC_PREFIX + event.getEventType().toLowerCase();
                String eventJson = objectMapper.writeValueAsString(event);
                
                kafkaTemplate.send(topic, event.getEventId().toString(), eventJson);
                
                log.info("Published event: {} to topic: {}", event.getEventType(), topic);
            } catch (Exception e) {
                log.error("Error publishing event: {}", event.getEventType(), e);
                throw new RuntimeException("Failed to publish event", e);
            }
        });
    }
}
