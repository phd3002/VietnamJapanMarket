package com.ecommerce.g58.repository;
import com.ecommerce.g58.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OTPRepository extends JpaRepository<OTP, Integer> {
    OTP findByOtpCode(String otpCode);
}
