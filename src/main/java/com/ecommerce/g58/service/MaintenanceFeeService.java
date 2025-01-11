package com.ecommerce.g58.service;

import com.ecommerce.g58.entity.*;
import com.ecommerce.g58.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MaintenanceFeeService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceFeeService.class);

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ProductRepository productRepository;

    private static final BigDecimal MAINTENANCE_FEE_PERCENTAGE = new BigDecimal("0.05"); // 5%
    @Autowired
    private UserRepository userRepository;

    /**
     * Scheduled Task: Process Monthly Maintenance Fee
     * Chạy vào lúc 12:00 AM ngày 15 hàng tháng để tính toán và thu phí duy trì.
     */
//            @Scheduled(cron = "*/10 * * * * ?") // run every 10s
    @Scheduled(cron = "0 0 0 15 * ?") // Chạy vào lúc 12:00 AM ngày 15 hàng tháng
    @Transactional
    public void processMonthlyMaintenanceFee() {
        logger.info("Bắt đầu xử lý phí duy trì hàng tháng.");

        List<Stores> allStores = storeRepository.findAll();

        for (Stores store : allStores) {
            // Tính doanh thu hàng tháng
            BigDecimal monthlyRevenue = calculateMonthlyRevenue(store.getStoreId());

            // Tính phí duy trì (5% doanh thu)
            BigDecimal maintenanceFee = monthlyRevenue.multiply(MAINTENANCE_FEE_PERCENTAGE).setScale(2, BigDecimal.ROUND_HALF_UP);

            // Lấy ví của người bán (owner)
            Users owner = store.getOwnerId();
            if (owner == null) {
                logger.warn("Không tìm thấy chủ cửa hàng cho storeId: {}", store.getStoreId());
                continue;
            }

            Wallet sellerWallet = walletRepository.findByUserId(owner).orElse(null);
            if (sellerWallet == null) {
                // Log lỗi: không tìm thấy ví của người bán
                logger.warn("Không tìm thấy ví cho người bán: {}", owner.getEmail());
                continue;
            }

            BigDecimal walletBalance = BigDecimal.valueOf(sellerWallet.getBalance());

            if (walletBalance.compareTo(maintenanceFee) >= 0) {
                // Trừ phí duy trì
                sellerWallet.setBalance(walletBalance.subtract(maintenanceFee).longValue());
                walletRepository.save(sellerWallet);

                // Ghi nhận giao dịch
                Transactions transaction = new Transactions();
                transaction.setFromWalletId(sellerWallet);
                transaction.setAmount(maintenanceFee.longValue());
                transaction.setPreviousBalance(walletBalance.longValue());
                transaction.setTransactionType("Maintenance Fee");
                transaction.setDescription("Trừ phí duy trì 5% doanh thu tháng " + LocalDate.now().getMonth());
                transaction.setCreatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);

                // Cập nhật ngày thanh toán cuối và trạng thái
                store.setLastMaintenanceFeeDate(LocalDate.now());
                store.setMaintenanceFeeStatus("PAID");
                store.setLockedUntil(null); // Mở khóa nếu trước đó bị khóa
                storeRepository.save(store);

                // Đảm bảo các sản phẩm của cửa hàng được hiển thị
                List<Products> products = store.getProducts();
                if (products != null) {
                    for (Products product : products) {
                        product.setVisible(true);
                    }
                    productRepository.saveAll(products);
                }

                // Gửi email xác nhận thanh toán (nếu cần)
                sendMaintenanceFeePaidEmail(store);

                // Log thông báo thành công
                logger.info("Trừ phí duy trì thành công cho storeId: {}", store.getStoreId());
            } else {
                // Không đủ số dư, gửi email cảnh báo
                sendMaintenanceFeeWarningEmail(store, maintenanceFee);

                // Cập nhật trạng thái phí duy trì là PENDING và đặt thời gian khóa là 7 ngày sau
                store.setMaintenanceFeeStatus("PENDING");
                store.setLockedUntil(LocalDateTime.now().plusDays(7));
                storeRepository.save(store);

                // Ẩn sản phẩm ngay lập tức khi trạng thái PENDING
                hideStoreProducts(store);

                // Log thông báo cảnh báo
                logger.info("Gửi cảnh báo phí duy trì cho storeId: {}", store.getStoreId());
            }
        }

        // Log hoàn thành xử lý
        logger.info("Hoàn tất xử lý phí duy trì hàng tháng.");
    }

    /**
     * Scheduled Task: Check Overdue Maintenance Fees
     * Chạy hàng ngày vào lúc 1:00 AM để kiểm tra và khóa cửa hàng nếu cần.
     */
    @Scheduled(cron = "0 0 1 * * ?") // Chạy vào lúc 1:00 AM hàng ngày
//            @Scheduled(cron = "*/10 * * * * ?") // run every 10s
    @Transactional
    public void checkOverdueMaintenanceFees() {
        logger.info("Bắt đầu kiểm tra phí duy trì quá hạn.");

        LocalDateTime now = LocalDateTime.now();
        List<Stores> pendingStores = storeRepository.findByMaintenanceFeeStatus("PENDING");

        for (Stores store : pendingStores) {
            if (store.getLockedUntil() != null && now.isAfter(store.getLockedUntil())) {
                // Khóa cửa hàng
                lockStore(store);

                // Hoàn trả đơn hàng chưa hoàn thành
                refundIncompleteOrders(store);

                // Cập nhật trạng thái phí duy trì là LOCKED
                store.setMaintenanceFeeStatus("LOCKED");
                storeRepository.save(store);
                logger.info("Khóa cửa hàng thành công cho storeId: {}", store.getStoreId());
                Users owner = store.getOwnerId();
                owner.setStatus("inactive");
                userRepository.save(owner);
                logger.info("Khóa tài khoảm thành công cho user: {}", owner.getUserId());
                // Log thông báo khóa

            }
        }

        // Log hoàn thành kiểm tra
        logger.info("Hoàn tất kiểm tra phí duy trì quá hạn.");
    }

    /**
     * Tính doanh thu hàng tháng của cửa hàng.
     */
    private BigDecimal calculateMonthlyRevenue(Integer storeId) {
        // Tính khoảng thời gian từ đầu tháng trước đến cuối tháng trước
        LocalDateTime now = LocalDateTime.now();
        LocalDate lastMonthDate = now.minusMonths(1).toLocalDate();

        // Tính đầu tháng trước
        LocalDateTime startOfMonth = lastMonthDate.withDayOfMonth(1).atStartOfDay();

        // Tính cuối tháng trước
        LocalDate lastMonthEndDate = lastMonthDate.withDayOfMonth(lastMonthDate.lengthOfMonth());
        LocalDateTime endOfMonth = lastMonthEndDate.atTime(23, 59, 59);

        // Lấy doanh thu từ OrderService
        BigDecimal revenue = orderService.calculateRevenueForStore(storeId, startOfMonth, endOfMonth);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    /**
     * Gửi email cảnh báo phí duy trì cho người bán.
     */
    private void sendMaintenanceFeeWarningEmail(Stores store, BigDecimal maintenanceFee) {
        Users owner = store.getOwnerId();
        if (owner == null) return;
        String email = owner.getEmail();

        String subject = "[VJ-Market] Cảnh báo phí duy trì cửa hàng";
        String body = "<p>Xin chào " + owner.getFirstName() + ",</p>"
                + "<p>Cửa hàng <b>" + store.getStoreName() + "</b> của bạn chưa thanh toán phí duy trì hàng tháng 5% doanh thu.</p>"
                + "<p>Số tiền cần thanh toán: " + maintenanceFee.toString() + " VND</p>"
                + "<p>Bạn có 7 ngày để thanh toán phí duy trì. Sau đó, sản phẩm của cửa hàng sẽ không được hiển thị và tài khoản/cửa hàng của bạn sẽ bị khóa.</p>"
                + "<p>Vui lòng nạp tiền vào ví để thanh toán phí duy trì.</p>"
                + "<p>Trân trọng,</p>"
                + "<p>Đội ngũ VJ-Market</p>";

        emailService.sendEmail(email, subject, body);
    }

    /**
     * Gửi email xác nhận thanh toán phí duy trì.
     */
    private void sendMaintenanceFeePaidEmail(Stores store) {
        Users owner = store.getOwnerId();
        if (owner == null) return;
        String email = owner.getEmail();

        String subject = "[VJ-Market] Phí duy trì cửa hàng đã được thanh toán";
        String body = "<p>Xin chào " + owner.getFirstName() + ",</p>"
                + "<p>Cửa hàng <b>" + store.getStoreName() + "</b> của bạn đã thanh toán phí duy trì thành công.</p>"
                + "<p>Cửa hàng của bạn đã được mở khóa và sản phẩm sẽ hiển thị lại trên trang web.</p>"
                + "<p>Trân trọng,</p>"
                + "<p>Đội ngũ VJ-Market</p>";

        emailService.sendEmail(email, subject, body);
    }

    /**
     * Ẩn tất cả sản phẩm của cửa hàng khi trạng thái PENDING.
     */
    private void hideStoreProducts(Stores store) {
        List<Products> products = store.getProducts();
        if (products != null) {
            for (Products product : products) {
                product.setVisible(false);
            }
            productRepository.saveAll(products);
        }
    }

    /**
     * Khóa cửa hàng: Ẩn tất cả sản phẩm và gửi email thông báo.
     */
    private void lockStore(Stores store) {
        // Ẩn tất cả sản phẩm
        hideStoreProducts(store);

        // Gửi email thông báo khóa cửa hàng
        sendStoreLockedEmail(store);
    }

    /**
     * Gửi email thông báo khóa cửa hàng cho người bán.
     */
    private void sendStoreLockedEmail(Stores store) {
        Users owner = store.getOwnerId();
        if (owner == null) return;
        String email = owner.getEmail();

        String subject = "[VJ-Market] Cửa hàng của bạn đã bị khóa";
        String body = "<p>Xin chào " + owner.getFirstName() + ",</p>"
                + "<p>Cửa hàng <b>" + store.getStoreName() + "</b> của bạn đã bị khóa do không thanh toán phí duy trì trong thời hạn 7 ngày.</p>"
                + "<p>Vui lòng thanh toán phí duy trì để mở khóa cửa hàng.</p>"
                + "<p>Nếu bạn không thanh toán trong thời gian quy định, tài khoản/cửa hàng của bạn có thể bị khóa vĩnh viễn.</p>"
                + "<p>Trân trọng,</p>"
                + "<p>Đội ngũ VJ-Market</p>";

        emailService.sendEmail(email, subject, body);
    }

    /**
     * Hoàn trả các đơn hàng chưa hoàn thành của cửa hàng bị khóa.
     */
    private void refundIncompleteOrders(Stores store) {
        // Tìm các đơn hàng chưa hoàn thành của cửa hàng
        List<Orders> incompleteOrders = orderService.findIncompleteOrdersByStore(store.getStoreId());
        for (Orders order : incompleteOrders) {
            // Hoàn trả đơn hàng
            orderDetailService.refundOrder(order.getOrderId());
        }
    }
}
