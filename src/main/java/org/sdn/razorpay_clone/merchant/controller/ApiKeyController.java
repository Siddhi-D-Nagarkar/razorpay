package org.sdn.razorpay_clone.merchant.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.merchant.dto.request.CreateApiKeyRequest;
import org.sdn.razorpay_clone.merchant.dto.response.ApiKeyCreateResponse;
import org.sdn.razorpay_clone.merchant.dto.response.ApiKeyResponse;
import org.sdn.razorpay_clone.merchant.entity.ApiKey;
import org.sdn.razorpay_clone.merchant.service.ApiKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/merchants/{merchantId}/api-keys")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ApiKeyController {

    ApiKeyService apiKeyService;

    @PostMapping()
    public ResponseEntity<ApiKeyCreateResponse> createApiKey(@PathVariable UUID merchantId,
                                                             @RequestBody @Valid CreateApiKeyRequest apiKey) {
        return ResponseEntity.status(
                HttpStatus.CREATED
        ).body(apiKeyService.createApiKey(merchantId, apiKey));
    }

    @GetMapping()
    public ResponseEntity<List<ApiKeyResponse>> getListOfApiKeys(@PathVariable UUID merchantId) {
        return ResponseEntity.status(HttpStatus.OK).body(apiKeyService.listByMerchant(merchantId));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID merchantId, @PathVariable UUID keyId) {
        this.apiKeyService.revoke(merchantId, keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotate(@PathVariable UUID merchantId, @PathVariable UUID keyId) {
        return ResponseEntity.ok(this.apiKeyService.rotate(merchantId, keyId));
    }
}
