package org.sdn.razorpay_clone.merchant.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.sdn.razorpay_clone.common.enums.MerchantStatus;
import org.sdn.razorpay_clone.common.enums.UserRole;
import org.sdn.razorpay_clone.common.exception.DuplicateResourceException;
import org.sdn.razorpay_clone.common.exception.ResourceNotFoundException;
import org.sdn.razorpay_clone.merchant.dto.request.LoginRequest;
import org.sdn.razorpay_clone.merchant.dto.request.MerchantSignUpRequest;
import org.sdn.razorpay_clone.merchant.dto.response.LoginResponse;
import org.sdn.razorpay_clone.merchant.dto.response.MerchantResponse;
import org.sdn.razorpay_clone.merchant.entity.AppUser;
import org.sdn.razorpay_clone.merchant.entity.Merchant;
import org.sdn.razorpay_clone.merchant.mapper.MerchantMapper;
import org.sdn.razorpay_clone.merchant.repository.AppUserRepository;
import org.sdn.razorpay_clone.merchant.repository.MerchantRepository;
import org.sdn.razorpay_clone.merchant.security.JwtUtil;
import org.sdn.razorpay_clone.merchant.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    AppUserRepository appUserRepository;
    MerchantRepository merchantRepository;
    MerchantMapper merchantMapper;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;

    @Transactional()
    @Override
    public MerchantResponse signup(MerchantSignUpRequest request) {
        if (merchantRepository.existsByEmail(request.email())) {
            log.error("Merchant with email {} already exists", request.email());
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL", "Merchant with email " + request.email() + " already exists");
        }

        Merchant newMerchant = merchantMapper.toMerchant(request, MerchantStatus.PENDING_KYC);

        newMerchant = merchantRepository.save(newMerchant);

        AppUser newAppUser = AppUser.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .merchant(newMerchant)
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(newAppUser);
        return merchantMapper.toMerchantResponse(newMerchant);

    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        AppUser appUser = appUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.email()));

        String token = jwtUtil.generateAccessToken(request.email(), appUser.getMerchant().getId(), appUser.getRole().toString());

        return new LoginResponse(token);
    }
}
