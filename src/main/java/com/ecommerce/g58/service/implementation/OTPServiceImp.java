package com.ecommerce.g58.service.implementation;
import com.ecommerce.g58.entity.OTP;
import com.ecommerce.g58.repository.OTPRepository;
import com.ecommerce.g58.service.OTPService;
import org.springframework.stereotype.Service;
@Service
public class OTPServiceImp implements OTPService {
    private final OTPRepository otpRepository;

    public OTPServiceImp(OTPRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Override
    public void saveOTP(OTP otp) {
        otpRepository.save(otp);
    }

    @Override
    public OTP findByOtpCode(String otpCode) {
        return otpRepository.findByOtpCode(otpCode);
    }
}
