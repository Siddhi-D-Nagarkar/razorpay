package org.sdn.razorpay_clone.merchant.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.sdn.razorpay_clone.common.exception.RateLimitException;
import org.sdn.razorpay_clone.common.ratelimit.RateLimitResult;
import org.sdn.razorpay_clone.common.ratelimit.RateLimiter;
import org.sdn.razorpay_clone.merchant.cache.ApiKeyCache;
import org.sdn.razorpay_clone.merchant.cache.ApiKeyCacheEntry;
import org.sdn.razorpay_clone.merchant.entity.ApiKey;
import org.sdn.razorpay_clone.merchant.repository.ApiKeyRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final ApiKeyRepository apiKeyRepository;
    //    PasswordEncoder passwordEncoder;
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ApiKeyCache apiKeyCache;
    private final RateLimiter rateLimiter;
    @Value("${app.rate-limit.use-case.api-key.requests-per-minute:10}")
    private Integer requestPerMinute;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Implement your API key authentication logic here
        // For example, you can check for the presence of an API key in the request headers
        log.info("Incoming request: {}", request.getRequestURI());

        try {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Basic ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String[] credentials = decodeHeader(header);
            if (credentials == null) {
                throw new BadRequestException("Malformed API Key Header");
            }

            String keyId = credentials[0];
            String secret = credentials[1];

            // You can implement your logic to validate the API key and secret here
            ApiKeyCacheEntry apiKeyCacheEntry = this.apiKeyCache.get(keyId)
                    .orElseGet(() -> this.loadAndCache(keyId));

//            ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
//                    .orElseThrow(() -> new BadRequestException("Invalid API Key"));

            RateLimitResult rateLimitResult = this.rateLimiter.check("apikey:" + keyId, requestPerMinute, 60);

            if (!rateLimitResult.allowed()) {
                log.warn("Rate limit exceeded for API Key: {}. Retry after {} seconds", keyId, rateLimitResult.retryAfterSeconds());
                throw new RateLimitException("Too many requests. Please try again later.", rateLimitResult.retryAfterSeconds());
            }

            response.setHeader("X-RateLimit-Limit", String.valueOf(requestPerMinute));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitResult.remaining()));

            if (apiKeyCacheEntry != null && !secretMatches(secret, apiKeyCacheEntry)) {
                throw new BadRequestException("Invalid API Key or Secret");
            }

            if (!apiKeyCacheEntry.enabled() || !this.secretMatches(secret, apiKeyCacheEntry)) {
                throw new BadRequestException("API Key is disabled or invalid");
            }

            var auth = new UsernamePasswordAuthenticationToken(keyId, null,
                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE"))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            merchantContext.setMerchantId(apiKeyCacheEntry.merchantId());
            merchantContext.setKeyId(apiKeyCacheEntry.keyId());

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            this.handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }

    private ApiKeyCacheEntry loadAndCache(String keyId) {
        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
                .orElse(null);

        if (apiKey == null) {
            return null;
        }
        ApiKeyCacheEntry apiKeyCacheEntry = ApiKeyCacheEntry.builder()
                .merchantId(apiKey.getMerchant().getId())
                .keyId(apiKey.getKeyId())
                .keySecretHash(apiKey.getKeySecretHash())
                .previousKeySecretHash(apiKey.getPreviousKeySecretHash())
                .environment(apiKey.getEnvironment())
                .gracePeriodExpiryAt(apiKey.getGracePeriodExpiryAt())
                .enabled(apiKey.getEnabled())
                .build();

        this.apiKeyCache.put(keyId, apiKeyCacheEntry);
        return apiKeyCacheEntry;
    }

    private Boolean secretMatches(String rawSecret, ApiKeyCacheEntry apiKey) {
        if (this.passwordEncoder.matches(rawSecret, apiKey.keySecretHash())) {
            return true;
        }
        return apiKey.isInGracePeriod() && apiKey.previousKeySecretHash() != null &&
                this.passwordEncoder.matches(rawSecret, apiKey.previousKeySecretHash());
    }

    private String[] decodeHeader(String header) {
        String encoded = header.substring("Basic ".length());
        // Implement base64 decoding logic here
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);

        int colonIndex = decoded.indexOf(':');
        if (colonIndex < 1) {
            return null;
        }
        return new String[]{decoded.substring(0, colonIndex), decoded.substring(colonIndex + 1)};
    }
}
