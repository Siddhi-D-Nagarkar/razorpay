package org.sdn.razorpay_clone.payment.outbox;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.enums.OutboxStatus;
import org.sdn.razorpay_clone.payment.entity.OutboxEvent;
import org.sdn.razorpay_clone.payment.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OutboxResultHandler {

    OutboxEventRepository outboxEventRepository;
    Integer MAX_ATTEMPT = 3;

    @Transactional
    public void handleEventPublished(OutboxEvent event) {
        // Implement any additional logic needed after the event is published
        log.info("Event {} published successfully.", event.getId());
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(LocalDateTime.now());
        this.outboxEventRepository.save(event);
    }

    @Transactional
    public void handleEventFailed(OutboxEvent event, String errorMessage) {
        log.info("Event {} failed.", event.getId());
        event.setLastError(errorMessage.length() < 1000 ? errorMessage : errorMessage.substring(0, 1000));
        event.setAttempts(event.getAttempts() + 1);
        if (event.getAttempts() >= MAX_ATTEMPT) {
            event.setStatus(OutboxStatus.FAILED);
        }
        this.outboxEventRepository.save(event);
    }
}
