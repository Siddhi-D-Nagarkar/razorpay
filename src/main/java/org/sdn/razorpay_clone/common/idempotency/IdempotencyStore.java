package org.sdn.razorpay_clone.common.idempotency;

import java.time.Duration;
import java.util.Optional;

public interface IdempotencyStore {
    String IN_PROCESS = "__IN_PROCESS__";

    boolean setIfAbsent(String key, Duration ttl);

    void store(String key, String value, Duration ttl);

    Optional<String> get(String key);

    void delete(String key);

}
