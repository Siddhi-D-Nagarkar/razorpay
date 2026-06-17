package org.sdn.razorpay_clone.payment.repository;

import org.sdn.razorpay_clone.payment.entity.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderRecord, UUID> {
}