package org.sdn.razorpay_clone.merchant.controller;


import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.sdn.razorpay_clone.merchant.dto.request.MerchantSignUpRequest;
import org.sdn.razorpay_clone.merchant.dto.response.MerchantResponse;
import org.sdn.razorpay_clone.merchant.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/auth")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class AuthController {
    AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MerchantResponse> signup(@RequestBody @Valid  MerchantSignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));

    }
}
