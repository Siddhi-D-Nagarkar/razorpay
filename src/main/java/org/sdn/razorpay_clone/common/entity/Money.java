package org.sdn.razorpay_clone.common.entity;


import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Embeddable
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class Money {
        int amountUnits; // smallest unit of a currency
        String currency; // Rupee,USD,Yuan

//        MoneyObject Creation
        public static Money of(int amountUnits, String currency) {
                return new Money(amountUnits, currency);
        }

//        INR Money Object Creation
        public static Money inRupees(int amountUnits) {
                return new Money(amountUnits, "INR");
        }
        public Money add(Money money) {
                if (!this.currency.equals(money.currency)) {
                        throw new IllegalArgumentException("Currency mismatch");
                }
                return new Money(this.amountUnits + money.amountUnits, this.currency);
        }

        public Money subtract(Money money) {
                if (!this.currency.equals(money.currency)) {
                        throw new IllegalArgumentException("Currency mismatch");
                }
                return new Money(this.amountUnits - money.amountUnits, this.currency);
        }


}
