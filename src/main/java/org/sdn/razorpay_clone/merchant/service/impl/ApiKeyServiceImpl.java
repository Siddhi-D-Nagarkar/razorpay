package org.sdn.razorpay_clone.merchant.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.exception.ResourceNotFoundException;
import org.sdn.razorpay_clone.common.util.RandomizerUtil;
import org.sdn.razorpay_clone.merchant.dto.request.CreateApiKeyRequest;
import org.sdn.razorpay_clone.merchant.dto.response.ApiKeyCreateResponse;
import org.sdn.razorpay_clone.merchant.dto.response.ApiKeyResponse;
import org.sdn.razorpay_clone.merchant.entity.ApiKey;
import org.sdn.razorpay_clone.merchant.entity.Merchant;
import org.sdn.razorpay_clone.merchant.mapper.ApiKeyMapper;
import org.sdn.razorpay_clone.merchant.repository.ApiKeyRepository;
import org.sdn.razorpay_clone.merchant.repository.MerchantRepository;
import org.sdn.razorpay_clone.merchant.service.ApiKeyService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiKeyServiceImpl implements ApiKeyService {
    ApiKeyRepository apiKeyRepository;
    MerchantRepository merchantRepository;
    ApiKeyMapper apiKeyMapper;

    @Transactional
    @Override
    public ApiKeyCreateResponse createApiKey(UUID merchantId, CreateApiKeyRequest request) {
        Merchant merchant = this.merchantRepository.findById(merchantId).orElseThrow(() -> {
            log.error("Merchant with id {} not found", merchantId);
            return new ResourceNotFoundException("merchant", merchantId);
        });

        String keyId = "rzp_" + request.environment().name().toLowerCase() + "_" + RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(40);


        ApiKey newApiKey = ApiKey.builder()
                .keyId(keyId)
                .keySecretHash(rawSecret)
                .merchant(merchant)
                .environment(request.environment())
                .build();

        newApiKey = this.apiKeyRepository.save(newApiKey);

        return apiKeyMapper.toApiKeyCreateResponse(newApiKey, rawSecret);
    }

    @Override
    public List<ApiKeyResponse> listByMerchant(UUID merchantId) {
        return apiKeyMapper.toApiKeyResponseList(this.apiKeyRepository.findByMerchant_Id(merchantId));
    }

    @Transactional
    @Override
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey apiKey = this.apiKeyRepository.findByIdAndMerchant_Id(keyId, merchantId).orElseThrow(() -> {
            log.error("API Key with id {} not found for merchant {}", keyId, merchantId);
            return new ResourceNotFoundException("api_key", keyId);
        });

        apiKey.setEnabled(false);
    }

    @Transactional
    @Override
    public ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId) {
        ApiKey apiKey = this.apiKeyRepository.findByIdAndMerchant_Id(keyId, merchantId).orElseThrow(() -> {
            log.error("API Key with id {} not found for merchant {}", keyId, merchantId);
            return new ResourceNotFoundException("api_key", keyId);
        });

        if (!apiKey.getEnabled()) {
            log.error("API Key with id {} is disabled for merchant {}", keyId, merchantId);
            throw new RuntimeException("Cannot Rotate Disabled API Key");
        }

        String rawSecret = RandomizerUtil.randomBase64(40);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(rawSecret); // TODO :- Hash the secret before saving
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiryAt(LocalDateTime.now().plusHours(24)); // 24 grace period

        apiKey = this.apiKeyRepository.save(apiKey);
        return ApiKeyCreateResponse.builder()
                .id(apiKey.getId())
                .keyId(apiKey.getKeyId())
                .keySecret(rawSecret)
                .environment(apiKey.getEnvironment())
                .build();

    }


}
