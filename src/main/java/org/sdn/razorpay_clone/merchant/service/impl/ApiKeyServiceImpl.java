package org.sdn.razorpay_clone.merchant.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.exception.ResourceNotFoundException;
import org.sdn.razorpay_clone.merchant.dto.request.CreateApiKeyRequest;
import org.sdn.razorpay_clone.merchant.dto.response.ApiKeyCreateResponse;
import org.sdn.razorpay_clone.merchant.entity.ApiKey;
import org.sdn.razorpay_clone.merchant.entity.Merchant;
import org.sdn.razorpay_clone.merchant.repository.ApiKeyRepository;
import org.sdn.razorpay_clone.merchant.repository.MerchantRepository;
import org.sdn.razorpay_clone.merchant.service.ApiKeyService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiKeyServiceImpl implements ApiKeyService {
    ApiKeyRepository apiKeyRepository;
    MerchantRepository merchantRepository;

    @Override
    public ApiKeyCreateResponse createApiKey(UUID merchantId, CreateApiKeyRequest request) {
        Merchant merchant = this.merchantRepository.findById(merchantId).orElseThrow(() -> {
            log.error("Merchant with id {} not found", merchantId);
            return new ResourceNotFoundException("merchant", merchantId);
        });

        String keyId = "rzp_" + request.environment().name().toUpperCase() + "big_random_key";
        String rawSecret = "big_random_secret";


        ApiKey newApiKey = ApiKey.builder()
                .keyId(keyId)
                .keySecretHash(rawSecret)
                .merchant(merchant)
                .environment(request.environment())
                .build();

        newApiKey = this.apiKeyRepository.save(newApiKey);

        return ApiKeyCreateResponse.builder()
                .id(newApiKey.getId())
                .keyId(newApiKey.getKeyId())
                .keySecret(rawSecret)
                .environment(newApiKey.getEnvironment())
                .build();
    }


}
