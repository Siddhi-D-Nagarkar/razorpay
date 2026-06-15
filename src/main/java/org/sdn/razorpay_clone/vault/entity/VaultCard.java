package org.sdn.razorpay_clone.vault.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vault_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaultCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false, length = 4)
    String lastFour;
    @Column(nullable = false, length = 6)
    String bin; // Represent the first 6 digit of the card


    @Column(nullable = false)
    byte[] encryptedPan;
    @Column(nullable = false)
    byte[] encryptedDek;
    @Column(nullable = false)
    String brand;
    @Column(nullable = false)
    String expiryMonth;
    @Column(nullable = false)
    String expiryYear;
    @Column(nullable = false)
    String cardHolderName;
    LocalDateTime deletedAt;

}
