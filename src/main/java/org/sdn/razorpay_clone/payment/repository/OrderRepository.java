package org.sdn.razorpay_clone.payment.repository;

import jakarta.persistence.LockModeType;
import org.sdn.razorpay_clone.payment.entity.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderRecord, UUID> {

    boolean existsByMerchantIdAndReceipt(UUID merchantId, String receipt);

    Optional<OrderRecord> findByMerchantIdAndId(UUID merchantId, UUID orderId);

    Optional<OrderRecord> findByIdAndMerchantId(UUID uuid, UUID merchantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrderRecord o where o.id = :uuid and o.merchantId = :merchantId")
    Optional<OrderRecord> findByIdAndMerchantIdForUpdate(UUID uuid, UUID merchantId);

}