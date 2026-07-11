package org.sdn.razorpay_clone.payment.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.sdn.razorpay_clone.payment.gateway.PaymentStrategy;
import org.sdn.razorpay_clone.payment.gateway.strategy.CardPaymentStrategy;
import org.sdn.razorpay_clone.payment.gateway.strategy.NetBankingStrategy;
import org.sdn.razorpay_clone.payment.gateway.strategy.UpiPaymentStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentStrategyConfig {
    CardPaymentStrategy cardPaymentStrategy;
    UpiPaymentStrategy upiPaymentStrategy;
    NetBankingStrategy netBankingStrategy;


    @Bean
    public Map<PaymentMethod, PaymentStrategy> paymentStrategies() {
        return Map.of(
                PaymentMethod.CARD, cardPaymentStrategy,
                PaymentMethod.UPI, upiPaymentStrategy,
                PaymentMethod.NETBANKING, netBankingStrategy
        );
    }

}
