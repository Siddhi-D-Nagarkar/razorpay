package org.sdn.razorpay_clone.merchant.repository;

import org.sdn.razorpay_clone.merchant.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findByMerchant_Id(UUID merchantId);

    Optional<ApiKey> findByIdAndMerchant_Id(UUID keyId, UUID merchantId);
}