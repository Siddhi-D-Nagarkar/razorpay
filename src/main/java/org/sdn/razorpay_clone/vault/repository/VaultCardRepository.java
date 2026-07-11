package org.sdn.razorpay_clone.vault.repository;

import org.sdn.razorpay_clone.vault.entity.VaultCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VaultCardRepository extends JpaRepository<VaultCard, UUID> {
}