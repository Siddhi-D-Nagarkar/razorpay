package org.sdn.razorpay_clone.merchant.repository;

import org.sdn.razorpay_clone.merchant.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
}