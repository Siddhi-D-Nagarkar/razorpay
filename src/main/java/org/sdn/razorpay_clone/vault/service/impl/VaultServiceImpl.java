package org.sdn.razorpay_clone.vault.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.entity.Money;
import org.sdn.razorpay_clone.common.enums.CardBrand;
import org.sdn.razorpay_clone.common.exception.ResourceNotFoundException;
import org.sdn.razorpay_clone.common.util.RandomizerUtil;
import org.sdn.razorpay_clone.payment.gateway.PaymentGatewayRouter;
import org.sdn.razorpay_clone.payment.processor.PaymentProcessorRouter;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorRequest;
import org.sdn.razorpay_clone.payment.processor.dto.PaymentProcessorResponse;
import org.sdn.razorpay_clone.vault.config.VaultEncryptionConfig;
import org.sdn.razorpay_clone.vault.dto.request.TokenizeRequest;
import org.sdn.razorpay_clone.vault.dto.response.TokenizeResponse;
import org.sdn.razorpay_clone.vault.entity.CardToken;
import org.sdn.razorpay_clone.vault.entity.VaultCard;
import org.sdn.razorpay_clone.vault.repository.CardTokenRepository;
import org.sdn.razorpay_clone.vault.repository.VaultCardRepository;
import org.sdn.razorpay_clone.vault.service.VaultService;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VaultServiceImpl implements VaultService {
    CardTokenRepository cardTokenRepository;
    VaultCardRepository vaultCardRepository;
    BytesEncryptor dekEncrypter;
    PaymentProcessorRouter paymentProcessorRouter;


    @Transactional
    @Override
    public TokenizeResponse tokenize(TokenizeRequest tokenizeRequest, UUID merchantId) {
        String lastFour = tokenizeRequest.pan().substring(tokenizeRequest.pan().length() - 4);
        String bin = tokenizeRequest.pan().substring(0, 6);
        CardBrand cardBrand = detectBrand(tokenizeRequest.pan());

        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig.panEncrypter(dek).encrypt(tokenizeRequest.pan().getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDek = dekEncrypter.encrypt(dek);

        VaultCard vaultCard = vaultCardRepository.save(VaultCard.builder()
                .brand(cardBrand)
                .encryptedPan(encryptedPan)
                .encryptedDek(encryptedDek)
                .expiryMonth(String.valueOf(tokenizeRequest.expiryMonth()))
                .expiryYear(String.valueOf(tokenizeRequest.expiryYear()))
                .bin(bin)
                .lastFour(lastFour)
                .cardHolderName(tokenizeRequest.cardHolderName())
                .build());

        String token = RandomizerUtil.randomBase64(32);
        cardTokenRepository.save(CardToken.builder()
                .token(token)
                .vaultCard(vaultCard)
                .merchant(merchantId)
                .customer(tokenizeRequest.customerId())
                .build());


        return TokenizeResponse.builder()
                .expiryYear(tokenizeRequest.expiryYear())
                .expiryMonth(tokenizeRequest.expiryMonth())
                .lastFour(lastFour)
                .brand(cardBrand)
                .token(token)
                .build();
    }

    @Override
    public PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails) {
        CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(() -> new ResourceNotFoundException("CardToken", token));

        VaultCard vaultCard = cardToken.getVaultCard();
        byte[] panBytes = null;
        try {

            byte[] dek = dekEncrypter.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncrypter(dek).decrypt(vaultCard.getEncryptedPan());
            String pan = new String(panBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.card(paymentId, pan, expiry, amount, methodDetails);
            PaymentProcessorResponse response = paymentProcessorRouter.charge(paymentProcessorRequest);

            log.info("Vault charge registered, token={}", token.substring(0, 4));

            return response;
        } catch (Exception e) {
            log.warn("Vault charge failed, token={}****, error={}", token.substring(0, 4), e.getMessage());
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED", e.getMessage());
        } finally {
            if (panBytes != null) {
                Arrays.fill(panBytes, (byte) 0);
            }
        }
    }

    private CardBrand detectBrand(String pan) {
        if (pan.startsWith("4")) return CardBrand.VISA;
        if (pan.startsWith("5") || pan.startsWith("2")) return CardBrand.MASTERCARD;
        if (pan.startsWith("37") || pan.startsWith("34")) return CardBrand.AMEX;
        return CardBrand.RUPAY;
    }
}
