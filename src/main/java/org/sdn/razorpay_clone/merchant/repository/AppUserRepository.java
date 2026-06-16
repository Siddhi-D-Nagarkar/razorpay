package org.sdn.razorpay_clone.merchant.repository;

import org.sdn.razorpay_clone.merchant.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
}