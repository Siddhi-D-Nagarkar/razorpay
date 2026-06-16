package org.sdn.razorpay_clone.merchant.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.enums.MerchantStatus;
import org.sdn.razorpay_clone.common.enums.UserRole;
import org.sdn.razorpay_clone.common.exception.DuplicateResourceException;
import org.sdn.razorpay_clone.merchant.dto.request.MerchantSignUpRequest;
import org.sdn.razorpay_clone.merchant.dto.response.MerchantResponse;
import org.sdn.razorpay_clone.merchant.entity.AppUser;
import org.sdn.razorpay_clone.merchant.entity.Merchant;
import org.sdn.razorpay_clone.merchant.repository.AppUserRepository;
import org.sdn.razorpay_clone.merchant.repository.MerchantRepository;
import org.sdn.razorpay_clone.merchant.service.AuthService;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    AppUserRepository appUserRepository;
    MerchantRepository merchantRepository;

    @Transactional
    @Override
    public MerchantResponse signup(MerchantSignUpRequest request) {
        if (merchantRepository.existsByEmail(request.email())) {
            log.error("Merchant with email {} already exists", request.email());
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL", "Merchant with email " + request.email() + " already exists");
        }

        Merchant newMerchant = Merchant.builder()
                .name(request.name())
                .email(request.email())
                .businessType(request.businessType())
                .businessName(request.businessName())
                .status(MerchantStatus.PENDING_KYC)
                .build();

        newMerchant = merchantRepository.save(newMerchant);

        AppUser newAppUser = AppUser.builder()
                .email(request.email())
                .passwordHash(request.password()) // TODO :- encrypt password using Bcrypt
                .merchant(newMerchant)
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(newAppUser);

//        return new MerchantResponse(newMerchant.getId(), newMerchant.getName(), newMerchant.getEmail(),
//                newMerchant.getBusinessType(), newMerchant.getBusinessName(), newMerchant.getStatus());

        return MerchantResponse.builder()
                .id(newMerchant.getId())
                .name(newMerchant.getName())
                .email(newMerchant.getEmail())
                .businessType(newMerchant.getBusinessType())
                .businessName(newMerchant.getBusinessName())
                .status(newMerchant.getStatus())
                .build();

    }
}
