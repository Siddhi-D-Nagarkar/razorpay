package org.sdn.razorpay_clone.merchant.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.common.entity.BaseEntity;
import org.sdn.razorpay_clone.common.enums.UserRole;

import java.util.UUID;

@Entity
@Table(name = "app_user", indexes = {
        @Index(name = "idx_app_user_merchant_id", columnList = "merchant_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    Merchant merchant;

    @Column(nullable = false, length = 50, unique = true)
    String email;

    @Column(nullable = false, unique = true)
    String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    UserRole role;

}
