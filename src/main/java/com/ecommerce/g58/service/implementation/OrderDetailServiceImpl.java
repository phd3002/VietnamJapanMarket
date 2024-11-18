package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.dto.OrderDetailDTO;
import com.ecommerce.g58.dto.OrderDetailManagerDTO;
import com.ecommerce.g58.entity.Feedback;
import com.ecommerce.g58.repository.*;
import com.ecommerce.g58.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final FeedbackRepository feedbackRepository;
    private final ProductVariationRepository productVariationRepository;
    private final UserRepository userRepository;
    private final ShippingStatusRepository shippingStatusRepository;

    @Autowired
    public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository, FeedbackRepository feedbackRepository, ProductVariationRepository productVariationRepository, UserRepository userRepository, ShippingStatusRepository shippingStatusRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.feedbackRepository = feedbackRepository;
        this.productVariationRepository = productVariationRepository;
        this.userRepository = userRepository;
        this.shippingStatusRepository = shippingStatusRepository;
    }

    @Override
    public List<OrderDetailDTO> getOrderDetails(Long orderId) {
        List<Object[]> results = orderDetailRepository.getOrderDetails(orderId);
        List<OrderDetailDTO> orderDetails = new ArrayList<>();

        for (Object[] result : results) {
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setOrderId(orderId);
            dto.setProductId(((Integer) result[1]).longValue());
            dto.setStoreId(((Integer) result[2]).longValue());
            dto.setProductName((String) result[3]);
            dto.setProductImage((String) result[4]);
            dto.setCategoryName((String) result[5]);
            dto.setSizeAndColor((String) result[6]);
//            dto.setOrderTotalPrice(((Integer) result[7]));
            if (result[7] instanceof BigInteger) {
                dto.setOrderTotalPrice(((BigInteger) result[7]).intValue());
            } else if (result[7] instanceof Integer) {
                dto.setOrderTotalPrice((Integer) result[7]);
            }
            dto.setQuantity((Integer) result[8]);
            dto.setProductTotalPrice((Integer) result[9]);
            dto.setAvgRating((Integer) result[10]);
            dto.setStoreName((String) result[11]);
            dto.setStoreImage((String) result[12]);  // Added
            dto.setTotalAmount((Integer) result[13]);
            dto.setShippingFee((Integer) result[14]);
            dto.setPaymentMethod((String) result[15]);
            dto.setPaymentStatus((String) result[16]);
            dto.setShippingAddress((String) result[17]);
            dto.setShippingStatus((String) result[18]);
            dto.setPreviousStatus((String) result[19]);
            dto.setTrackingNumber((String) result[20]);
            if (result[21] instanceof Timestamp) {
                Timestamp pendingTimestamp = (Timestamp) result[21];
                dto.setPendingTime(pendingTimestamp.toLocalDateTime());
            }
            if (result[22] instanceof Timestamp) {
                Timestamp confirmedTimestamp = (Timestamp) result[22];
                dto.setConfirmedTime(confirmedTimestamp.toLocalDateTime());
            }
            if (result[23] instanceof Timestamp) {
                Timestamp processingTimestamp = (Timestamp) result[23];
                dto.setProcessingTime(processingTimestamp.toLocalDateTime());
            }
            if (result[24] instanceof Timestamp) {
                Timestamp dispatchedTimestamp = (Timestamp) result[24];
                dto.setDispatchedTime(dispatchedTimestamp.toLocalDateTime());
            }
            if (result[25] instanceof Timestamp) {
                Timestamp shippingTimestamp = (Timestamp) result[25];
                dto.setShippingTime(shippingTimestamp.toLocalDateTime());
            }
            if(result[26] instanceof Timestamp) {
                Timestamp failedTimestamp = (Timestamp) result[26];
                dto.setFailedTime(failedTimestamp.toLocalDateTime());
            }
            if (result[27] instanceof Timestamp) {
                Timestamp deliveredTimestamp = (Timestamp) result[27];
                dto.setDeliveredTime(deliveredTimestamp.toLocalDateTime());
            }
            if (result[28] instanceof Timestamp) {
                Timestamp completedTimestamp = (Timestamp) result[28];
                dto.setCompletedTime(completedTimestamp.toLocalDateTime());
            }
            if (result[29] instanceof Timestamp) {
                Timestamp cancelledTimestamp = (Timestamp) result[29];
                dto.setCancelledTime(cancelledTimestamp.toLocalDateTime());
            }
            if (result[30] instanceof Timestamp) {
                Timestamp returnedTimestamp = (Timestamp) result[30];
                dto.setReturnedTime(returnedTimestamp.toLocalDateTime());
            }
            orderDetails.add(dto);
        }
        return orderDetails;
    }

    @Transactional
    @Override
    public void rateOrder(Long orderId, String userEmail, String rateText, Integer rateStar) {
        List<OrderDetailDTO> orderDetails = getOrderDetails(orderId);
        if (orderDetails.isEmpty()) throw new RuntimeException();

        var user = userRepository.findByEmail(userEmail);
        if (user == null) throw new RuntimeException();

        var orderDetail = orderDetails.get(0);
        var productId = orderDetail.getProductId();
        var productVariants = productVariationRepository.findByProductIdProductId(productId.intValue());

        for (var pv : productVariants) {
            var storeId = pv.getProductId().getStoreId();
            var feedback = new Feedback();
            feedback.setStoreId(storeId);
            feedback.setUserId(user);
            feedback.setRating(rateStar);
            feedback.setComment(rateText);
            feedback.setVariationId(pv);
            feedback.setCreatedAt(LocalDateTime.now());
            feedbackRepository.save(feedback);
        }
    }

    @Override
    @Transactional
    public void updateStatus(Integer orderId, String status, String reason) {
        var orderStatus = shippingStatusRepository.findByOrderIdOrderId(orderId);
        if (orderStatus.getStatus().equals("Pending") || orderStatus.getStatus().equals("Processing")
                || (orderStatus.getStatus().equals("Delivered") && reason != null)) {
            orderStatus.setPreviousStatus(orderStatus.getStatus());
        }
        orderStatus.setStatus(status);
        if (status.equals("Returned") && reason != null) {
            orderStatus.setReason(reason);
        }
    }

    @Override
    public List<OrderDetailManagerDTO> getOrderDetailsForManager(Long orderId) {
        List<Object[]> results = orderDetailRepository.getOrderDetailsByOrderId(orderId);
        List<OrderDetailManagerDTO> orderDetailManager = new ArrayList<>();
        for (Object[] result : results) {
            OrderDetailManagerDTO dto = new OrderDetailManagerDTO();
            dto.setStoreName((String) result[0]);
            dto.setStoreAddress((String) result[1]);
            dto.setStorePhone((String) result[2]);
            dto.setOrderStatus((String) result[3]);
            dto.setTrackingNumber((String) result[4]);
//            dto.setStatusTime((LocalDateTime) result[5]);
            if (result[5] instanceof Timestamp) {
                Timestamp statusTimestamp = (Timestamp) result[5];
                dto.setStatusTime(statusTimestamp.toLocalDateTime());
            }
            dto.setCustomerName((String) result[6]);
            dto.setCustomerAddress((String) result[7]);
            dto.setCustomerPhone((String) result[8]);
//            dto.setOrderDate((LocalDateTime) result[9]);
            if (result[9] instanceof Timestamp) {
                Timestamp orderTimestamp = (Timestamp) result[9];
                dto.setOrderDate(orderTimestamp.toLocalDateTime());
            }
            dto.setProductName((String) result[10]);
            dto.setProductType((String) result[11]);
            dto.setQuantity((Integer) result[12]);
            dto.setProductPrice((Integer) result[13]);
//            dto.setTotalPrice((Integer) result[14]);
            if (result[14] instanceof BigInteger) {
                dto.setTotalPrice(((BigInteger) result[14]).intValue());
            } else if (result[14] instanceof Integer) {
                dto.setTotalPrice((Integer) result[14]);
            }
            dto.setShippingFee((Integer) result[15]);
            dto.setTax((Integer) result[16]);
            dto.setPaymentMethod((String) result[17]);
//            dto.setTotalAmount((Integer) result[18]);
            if (result[18] instanceof BigDecimal) {
                dto.setTotalAmount(((BigDecimal) result[18]).intValue());
            } else if (result[18] instanceof Integer) {
                dto.setTotalAmount((Integer) result[18]);
            }
            orderDetailManager.add(dto);
        }
        return orderDetailManager;
    }
}

