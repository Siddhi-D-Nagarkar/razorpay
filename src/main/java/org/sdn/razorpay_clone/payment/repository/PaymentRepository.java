package org.sdn.razorpay_clone.payment.repository;

import org.sdn.razorpay_clone.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrderId(UUID id);
}