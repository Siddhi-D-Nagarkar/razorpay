package org.sdn.razorpay_clone.common.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.exception.IdempotencyConflictException;
import org.sdn.razorpay_clone.merchant.security.MerchantContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdempotencyFilter extends OncePerRequestFilter {
    static Set<String> IDEMPOTENCY_REQUIRED_METHODS = Set.of("POST", "PUT", "PATCH");
    static Duration IN_PROGRESS_TTL = Duration.ofSeconds(30);
    static Duration COMPLETED_TTL = Duration.ofHours(24);
    static String SEPARATOR = "|";
    IdempotencyStore idempotencyStore;
    MerchantContext merchantContext;
    HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!IDEMPOTENCY_REQUIRED_METHODS.contains(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String idempotencyKey = request.getHeader("X-Idempotency-Key");
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID merchantId = this.merchantContext.getMerchantId();
        String idempotentStoreKey = merchantId != null ? merchantId.toString() + ":" + idempotencyKey : idempotencyKey;

        boolean claimed = this.idempotencyStore.setIfAbsent(idempotentStoreKey, IN_PROGRESS_TTL);

        if (!claimed) {
            // Another thread has already claimed the key
            Optional<String> existingValue = this.idempotencyStore.get(idempotentStoreKey);
            if (existingValue.isPresent() && !IdempotencyStore.IN_PROCESS.equals(existingValue.get())) {
                // so if the value of the key is not IN Progress which means the actual value is coming
                replay(request, response, existingValue.get());
            } else {
                // So it means the another thread is working on it
                var ex = new IdempotencyConflictException("A request with this idempotency key is in progress");
                handlerExceptionResolver.resolveException(request, response, null, ex);
            }
            return;
        }

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(request, wrappedResponse);
        } finally {
            int status = wrappedResponse.getStatus();
            byte[] bodyBytes = wrappedResponse.getContentAsByteArray();
            String body = new String(bodyBytes, StandardCharsets.UTF_8);

            if (status < 400 && bodyBytes.length > 0) {
                // Success — store the completed response for future replays
                String stored = status + SEPARATOR + body;
                idempotencyStore.store(idempotentStoreKey, stored, COMPLETED_TTL);
                log.debug("IdempotencyFilter: stored response status={} key={}", status, idempotentStoreKey);
            } else {
                // Error or empty — delete placeholder so client can retry cleanly
                idempotencyStore.delete(idempotentStoreKey);
                log.debug("IdempotencyFilter: deleted placeholder after error status={} key={}", status, idempotentStoreKey);
            }
            // Always flush buffered body to the actual response.
            // If this is skipped the client receives an empty body.
            wrappedResponse.copyBodyToResponse();
        }


    }

    private void replay(HttpServletRequest request, HttpServletResponse response, String stored) throws IOException {
        int separatorIndex = stored.indexOf(SEPARATOR);
        if (separatorIndex < 0) {
            var ex = new IdempotencyConflictException("A request with this idempotency key is in progress");
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }

        int status = Integer.parseInt(stored.substring(0, separatorIndex));
        String body = stored.substring(separatorIndex + 1);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }

}
