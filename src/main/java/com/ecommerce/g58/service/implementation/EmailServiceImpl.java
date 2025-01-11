package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.Invoice;
import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import com.ecommerce.g58.enums.PaymentMethod;
import com.ecommerce.g58.repository.UserRepository;
import com.ecommerce.g58.service.EmailService;
import com.ecommerce.g58.utils.FormatVND;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.ecommerce.g58.entity.Transactions;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Override
    public void sendEmail(String email, String subject, String body) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("No user found with email: " + email);
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendTransactionMailAsync(Users user, Transactions transaction, Long amount) {
        // Tạo luồng riêng
        new Thread(() -> {
            try {
                // Ví dụ chuẩn bị subject, body
                String subject = "[VJ-Market] Thông báo giao dịch ví";
                String body = "<p>Xin chào " + user.getFirstName() + ",</p>"
                        + "<p>Giao dịch của bạn vừa được thực hiện: "
                        + "<br><b>" + transaction.getTransactionType() + "</b> "
                        + " số tiền: " + FormatVND.formatCurrency(BigDecimal.valueOf(Math.abs(amount)))
                        + "<br>Số dư: " + user.getWallets().get(0).getBalanceFormated()
                        + "<br>Loại thanh toán: " + transaction.getTransactionType()
                        + "<br>Thời gian: " + transaction.getCreatedAt()
                        + "<br>Nội dung: " + transaction.getDescription()
                        + "</p>";

                // Gửi email
                sendEmail(user.getEmail(), subject, body);
            } catch (Exception e) {
                logger.error("Lỗi khi gửi email giao dịch: ", e);
            }
        }).start();
    }

    @Override
    public void sendCheckoutCompleteEmail(Users user, Stores store, Invoice invoice, PaymentMethod paymentMethod) {
        String subject = "Hoàn tất thanh toán";
        String body = "<p>Xin chào " + user.getFirstName() + ",</p>"
                + "<p>Quá trình thanh toán của bạn đã hoàn tất. Dưới đây là thông tin chi tiết của cửa hàng:</p>"
                + "<p>Địa chỉ cửa hàng: " + store.getStoreAddress() + "</p>"
                + "<p>Thành phố: " + store.getCity() + "</p>"
                + "<p>Quận/Huyện: " + store.getDistrict() + "</p>"
                + "<p>Mã bưu điện: " + store.getPostalCode() + "</p>"
                + "<p>Số điện thoại cửa hàng: " + store.getStorePhone() + "</p>"
                + "<p><b>Thông tin thanh toán:</b></p>"
                + "<p>Số tiền đã thanh toán: " + FormatVND.formatCurrency(invoice.getDeposit()) + "</p>"
                + "<p>Tổng số tiền: " + FormatVND.formatCurrency(invoice.getTotalAmount()) + "</p>"
                + "<p>Phí vận chuyển: " + FormatVND.formatCurrency(invoice.getShippingFee()) + "</p>"
                + "<p>Thuế: " + FormatVND.formatCurrency(invoice.getTax()) + "</p>"
                + "<p>Số tiền còn lại cần thanh toán: " + FormatVND.formatCurrency(invoice.getRemainingBalance()) + "</p>"
                + "<p>Phương thức thanh toán: " + paymentMethod + "</p>"
                + "<p>Chúng tôi chân thành cảm ơn bạn đã tin tưởng và sử dụng dịch vụ của chúng tôi. Sự hài lòng của bạn là niềm vinh hạnh của chúng tôi.</p>";

        sendEmail(user.getEmail(), subject, body);
    }
}
