package org.sdn.razorpay_clone.merchant.service;

import jakarta.validation.Valid;
import org.sdn.razorpay_clone.merchant.dto.request.CreateApiKeyRequest;
import org.sdn.razorpay_clone.merchant.dto.response.ApiKeyCreateResponse;
import org.sdn.razorpay_clone.merchant.dto.response.ApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse createApiKey(UUID merchantId, CreateApiKeyRequest apiKey);

    List<ApiKeyResponse> listByMerchant(UUID merchantId);

    void revoke(UUID merchantId, UUID keyId);

    ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId);
}
