package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.OTP;
import org.springframework.stereotype.Service;

public interface OTPService {
    void saveOTP(OTP otp);
    OTP findByOtpCode(String otpCode);
}
