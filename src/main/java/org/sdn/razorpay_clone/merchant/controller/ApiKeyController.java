package org.sdn.razorpay_clone.merchant.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.merchant.dto.request.CreateApiKeyRequest;
import org.sdn.razorpay_clone.merchant.dto.response.ApiKeyCreateResponse;
import org.sdn.razorpay_clone.merchant.dto.response.ApiKeyResponse;
import org.sdn.razorpay_clone.merchant.entity.ApiKey;
import org.sdn.razorpay_clone.merchant.security.MerchantContext;
import org.sdn.razorpay_clone.merchant.service.ApiKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/merchants/api-keys")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ApiKeyController {

    ApiKeyService apiKeyService;
    MerchantContext merchantContext;

    @PostMapping()
    public ResponseEntity<ApiKeyCreateResponse> createApiKey(
            @RequestBody @Valid CreateApiKeyRequest apiKey) {
        return ResponseEntity.status(
                HttpStatus.CREATED
        ).body(apiKeyService.createApiKey(merchantContext.getMerchantId(), apiKey));
    }

    @GetMapping()
    public ResponseEntity<List<ApiKeyResponse>> getListOfApiKeys() {
        return ResponseEntity.status(HttpStatus.OK).body(apiKeyService.listByMerchant(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID keyId) {
        this.apiKeyService.revoke(merchantContext.getMerchantId(), keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotate(@PathVariable UUID keyId) {
        return ResponseEntity.ok(this.apiKeyService.rotate(merchantContext.getMerchantId(), keyId));
    }
}
