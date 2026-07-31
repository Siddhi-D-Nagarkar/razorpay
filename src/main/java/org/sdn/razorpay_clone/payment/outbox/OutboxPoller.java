package org.sdn.razorpay_clone.payment.outbox;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.conifg.KafkaProperties;
import org.sdn.razorpay_clone.common.enums.OutboxStatus;
import org.sdn.razorpay_clone.payment.entity.OutboxEvent;
import org.sdn.razorpay_clone.payment.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OutboxPoller {
    OutboxEventRepository outboxEventRepository;
    KafkaTemplate<String, Object> kafkaTemplate;
    KafkaProperties kafkaProperties;
    OutboxResultHandler outboxResultHandler;


    @Scheduled(fixedDelay = 5000) // Poll every 5 seconds
    public void poll() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                // Publish to Kafka
                String topic = kafkaProperties.topicFor(event.getAggregateType());
                String key = extractMerchantId(event.getPayload());

                Map<String, Object> envelope = Map.of(
                        "eventType", event.getEventType(),
                        "aggregateType", event.getAggregateType(),
                        "aggregateId", event.getAggregateId(),
                        "payload", event.getPayload()
                );

                kafkaTemplate.send(topic, key, envelope).get(5, TimeUnit.SECONDS);

                outboxResultHandler.handleEventPublished(event);

            } catch (Exception e) {
                log.error("Failed to publish event {}: {}", event.getId(), e.getMessage());
                // Update status to FAILED and increment attempts
                outboxResultHandler.handleEventFailed(event, e.getMessage());
            }
        }
    }

    private String extractMerchantId(Map<String, Object> payload) {
        Object merchantId = payload.get("merchantId");
        return merchantId != null ? merchantId.toString() : "unknown";
    }
}
