package com.ecommerce.g58.service.implementation;

import com.ecommerce.g58.enums.PaymentMethod;
import com.ecommerce.g58.utils.VNPayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class VNPayService {
    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.paymentUrl}")
    private String vnp_PaymentUrl;

    public String createPaymentUrl(int orderId, int amount, PaymentMethod paymentMethod, String returnUrl) {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(orderId));
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + orderId + " qua " + paymentMethod.getDisplayName());
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        String query = VNPayUtils.hashAllFields(vnp_Params, vnp_HashSecret);
        return vnp_PaymentUrl + "?" + query;
    }

    public String handlePaymentReturn(Map<String, String> params) {
        String responseCode = params.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            return "Thanh toán thành công";
        } else {
            return "Thanh toán thất bại: " + responseCode;
        }
    }
}
