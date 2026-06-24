package org.sdn.razorpay_clone.payment.config;

import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.sdn.razorpay_clone.payment.gateway.PaymentStrategy;
import org.sdn.razorpay_clone.payment.gateway.strategy.CardPaymentStrategy;
import org.sdn.razorpay_clone.payment.gateway.strategy.NetBankingStrategy;
import org.sdn.razorpay_clone.payment.gateway.strategy.UpiPaymentStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentStrategyConfig {


    @Bean
    public Map<PaymentMethod, PaymentStrategy> paymentStrategies() {
        return Map.of(
                PaymentMethod.CARD, new CardPaymentStrategy(),
                PaymentMethod.UPI, new UpiPaymentStrategy(),
                PaymentMethod.NETBANKING, new NetBankingStrategy()
        );
    }

}
