package org.sdn.razorpay_clone.merchant.service;

import jakarta.validation.Valid;
import org.sdn.razorpay_clone.merchant.dto.request.LoginRequest;
import org.sdn.razorpay_clone.merchant.dto.request.MerchantSignUpRequest;
import org.sdn.razorpay_clone.merchant.dto.response.LoginResponse;
import org.sdn.razorpay_clone.merchant.dto.response.MerchantResponse;

public interface AuthService {

    MerchantResponse signup(MerchantSignUpRequest request);

    LoginResponse login(LoginRequest request);
}
