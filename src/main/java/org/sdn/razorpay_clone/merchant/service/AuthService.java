package org.sdn.razorpay_clone.merchant.service;

import jakarta.validation.Valid;
import org.sdn.razorpay_clone.merchant.dto.request.MerchantSignUpRequest;
import org.sdn.razorpay_clone.merchant.dto.response.MerchantResponse;

public interface AuthService {

    MerchantResponse signup(MerchantSignUpRequest request);
}
