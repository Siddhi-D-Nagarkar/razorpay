package org.sdn.razorpay_clone.payment.repository;

import org.sdn.razorpay_clone.common.enums.OutboxStatus;
import org.sdn.razorpay_clone.payment.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}