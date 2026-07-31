package org.sdn.razorpay_clone.payment.outbox;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.enums.EventAggregateType;
import org.sdn.razorpay_clone.payment.entity.OutboxEvent;
import org.sdn.razorpay_clone.payment.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OutBoxEventService {
    OutboxEventRepository outboxEventRepository;

    public void storeEvent(EventAggregateType aggregateType, UUID aggregateId, String eventType,
                           Map<String, Object> eventPayload) {

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(eventPayload)
                .build();

        outboxEventRepository.save(outboxEvent);

    }


}
