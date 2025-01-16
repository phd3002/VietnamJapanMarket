package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.entity.*;
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
    public void sendEmailAsync(String to, String subject, String body) {
        new Thread(() -> {
            Users user = userRepository.findByEmail(to);
            if (user == null) {
                throw new IllegalArgumentException("No user found with email: " + to);
            }
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(body, true);
                mailSender.send(mimeMessage);
            } catch (MessagingException e) {
                throw new RuntimeException("Failed to send email", e);
            }
        }).start();
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

    @Override
    public void sendOrderStatusChangeEmail(Users user, Invoice invoice, Orders order, String status) {
        String vietnameseStatus = "";
        switch (status) {
            case "Pending":
                vietnameseStatus = "Đang chờ xử lý";
                break;
            case "Processing":
                vietnameseStatus = "Đang đóng gói";
                break;
            case "Confirmed":
                vietnameseStatus = "Đã xác nhận";
                break;
            case "Delivered":
                vietnameseStatus = "Đã giao hàng";
                break;
            case "Dispatched":
                vietnameseStatus = "Đã giao cho vận chuyển";
                break;
            case "Cancelled":
                vietnameseStatus = "Đã hủy";
                break;
            case "Rejected":
                vietnameseStatus = "Đã từ chối";
                break;
            case "Returned":
                vietnameseStatus = "Đã hoàn tiền";
                break;
            default:
                vietnameseStatus = status;
                break;
        }
        String subject = "Thay đổi trạng thái đơn hàng";
        String body = "<p>Xin chào " + user.getFirstName() + ",</p>"
                + "<p>Đơn hàng của bạn đã thay đổi trạng thái. Dưới đây là thông tin chi tiết:</p>"
                + "<p>Mã đơn hàng: " + order.getOrderCode() + "</p>"
                + "<p>Trạng thái đơn hàng: " + vietnameseStatus + "</p>"
                + "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>";
        sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public void sendWarningEmail(Users user, Orders order, ShippingStatus status) {
        String vietnameseStatus = "";
        switch (String.valueOf(status)) {
            case "Pending":
                vietnameseStatus = "Đang chờ xử lý";
                break;
            case "Processing":
                vietnameseStatus = "Đang đóng gói";
                break;
            case "Confirmed":
                vietnameseStatus = "Đã xác nhận";
                break;
            case "Delivered":
                vietnameseStatus = "Đã giao hàng";
                break;
            case "Dispatched":
                vietnameseStatus = "Đã giao cho vận chuyển";
                break;
            case "Cancelled":
                vietnameseStatus = "Đã hủy";
                break;
            case "Rejected":
                vietnameseStatus = "Đã từ chối";
                break;
            case "Returned":
                vietnameseStatus = "Đã hoàn tiền";
                break;
            default:
                vietnameseStatus = String.valueOf(status);
                break;
        }
        String subject = "Cảnh báo đơn hàng";
        String body = "<p>Xin chào " + user.getFirstName() + ",</p>"
                + "<p>Đơn hàng của bạn đã giao hàng thất bại do shipper không liên lạc được với người dùng. Dưới đây là thông tin chi tiết:</p>"
                + "<p>Mã đơn hàng: " + order.getOrderCode() + "</p>"
                + "<p>Trạng thái hiện tại: " + vietnameseStatus + "</p>"
                + "<p>Vui lòng để ý email và điên thoại để quá trình vận chuyển diễn ra suôn sẻ.</p>"
                + "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>";

        sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public void sendOrderCancellationEmail(Users user, Orders order) {
        String subject = "Hủy đơn hàng";
        String body = "<p>Xin chào " + user.getFirstName() + ",</p>"
                + "<p>Đơn hàng của bạn đã bị hủy do không giao được liên lạc được với người dùng. Dưới đây là thông tin chi tiết:</p>"
                + "<p>Mã đơn hàng: " + order.getOrderCode() + "</p>"
                + "<p>Do đó, đơn hàng đã bị hủy.</p>"
                + "<p>Số tiền hoàn: " + order.getPriceFormated() + "</p>"
                + "<p>Chúng tôi sẽ hoàn lại số tiền cho bạn trong thời gian sớm nhất.</p>"
                + "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>";
        sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public void sendOrderConfirmationEmail(Users user, Orders order) {
        String subject = "Xác nhận đơn hàng";
        String body = "<p>Xin chào " + user.getFirstName() + ",</p>"
                + "<p>Đơn hàng của bạn đã được xác nhận. Dưới đây là thông tin chi tiết:</p>"
                + "<p>Mã đơn hàng: " + order.getOrderCode() + "</p>"
                + "<p>Số tiền: " + order.getPriceFormated() + "</p>"
                + "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>";
        sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public void sendOrderDenialEmail(Users user, Orders order) {
        String subject = "Từ chối đơn hàng";
        String body = "<p>Xin chào " + user.getFirstName() + ",</p>"
                + "<p>Đơn hàng của bạn đã bị từ chối. Dưới đây là thông tin chi tiết:</p>"
                + "<p>Mã đơn hàng: " + order.getOrderCode() + "</p>"
                + "<p>Số tiền hoàn: " + order.getPriceFormated() + "</p>"
                + "<p>Chúng tôi sẽ hoàn lại số tiền cho bạn trong thời gian sớm nhất.</p>"
                + "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>";
        sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public void sendNotAsDescribedRefundEmail(Users user, Orders order) {
        String subject = "Hoàn tiền vì hàng không đúng mô tả";
        String body = "<p>Xin chào " + user.getFirstName() + ",</p>"
                + "<p>Chúng tôi xin thông báo rằng đơn hàng của bạn đã được hoàn tiền do hàng không đúng mô tả. Dưới đây là thông tin chi tiết:</p>"
                + "<p>Mã đơn hàng: " + order.getOrderCode() + "</p>"
                + "<p>Số tiền hoàn: " + order.getPriceFormated() + "</p>"
                + "<p>Chúng tôi sẽ hoàn lại tiền cho bạn trong thời gian sớm nhất.</p>"
                + "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>";
        sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public void sendDamagedRefundEmail(Users user, Orders order) {
        String subject = "Hoàn tiền vì hàng bị hỏng";
        String body = "<p>Xin chào " + user.getFirstName() + ",</p>"
                + "<p>Chúng tôi xin thông báo rằng đơn hàng của bạn đã được hoàn tiền do hàng bị hỏng. Dưới đây là thông tin chi tiết:</p>"
                + "<p>Mã đơn hàng: " + order.getOrderCode() + "</p>"
                + "<p>Số tiền hoàn: " + order.getPriceFormated() + "</p>"
                + "<p>Chúng tôi sẽ hoàn lại tiền cho bạn trong thời gian sớm nhất.</p>"
                + "<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.</p>";
        sendEmail(user.getEmail(), subject, body);
    }
}
