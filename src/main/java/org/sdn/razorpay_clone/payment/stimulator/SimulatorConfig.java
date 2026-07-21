package org.sdn.razorpay_clone.payment.stimulator;

import lombok.Getter;
import lombok.Setter;
import org.sdn.razorpay_clone.common.enums.ChaosMode;
import org.sdn.razorpay_clone.common.enums.PaymentMethod;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment.simulator")
public class SimulatorConfig {
    private Integer pollIntervalMs = 2000;
    private ChaosMode chaosMode = ChaosMode.NORMAL;
    private Map<String, MethodSimulatorConfig> methods = new HashMap<>();

    @Getter
    @Setter
    public static class MethodSimulatorConfig {
        private Integer minDelaySeconds = 1;
        private Integer maxDelaySeconds = 5;
        private Integer successRate = 80;
    }

    MethodSimulatorConfig configFor(PaymentMethod method) {
        return this.methods.getOrDefault(method.name(), new MethodSimulatorConfig());
    }
}
