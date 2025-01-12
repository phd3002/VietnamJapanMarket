package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class OtpService {
    private static final long OTP_EXPIRE_TIME_MS = 2 * 60 * 1000; // 2 phút

    @Autowired
    private HttpSession session;

    @Autowired
    private EmailService emailService; // service gửi email sẵn có

    public void generateAndSendOtp(Users user) {
        // Tạo 6 chữ số
        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));

        // Lưu vào session
        OtpData otpData = new OtpData(otp, System.currentTimeMillis() + OTP_EXPIRE_TIME_MS);
        session.setAttribute("OTP_DATA_" + user.getUserId(), otpData);

        // Gửi mail
        String subject = "[VJ-Market] Mã xác thực (OTP)";
        String body = "<p>Xin chào " + user.getFirstName() + ",</p>"
                + "<p>Mã OTP của bạn là: <b>" + otp + "</b></p>"
                + "<p>Mã có hiệu lực trong 2 phút.</p>";

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    public boolean verifyOtp(Users user, String inputOtp) {
        // Lấy otpData từ session
        OtpData otpData = (OtpData) session.getAttribute("OTP_DATA_" + user.getUserId());
        if (otpData == null) {
            return false;
        }
        // Kiểm tra hết hạn
        if (System.currentTimeMillis() > otpData.getExpireTime()) {
            // Hết hạn -> xóa session
            session.removeAttribute("OTP_DATA_" + user.getUserId());
            return false;
        }
        // So sánh OTP
        if (!otpData.getOtp().equals(inputOtp)) {
            return false;
        }
        // Xoá session sau khi dùng (tránh dùng lại)
        session.removeAttribute("OTP_DATA_" + user.getUserId());
        return true;
    }

    // Lớp phụ để lưu OTP
    private static class OtpData {
        private String otp;
        private long expireTime;
        // constructor, getter/setter
        public OtpData(String otp, long expireTime) {
            this.otp = otp;
            this.expireTime = expireTime;
        }
        public String getOtp() {
            return otp;
        }
        public long getExpireTime() {
            return expireTime;
        }
    }
}
