package com.ecommerce.g58.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatVND {
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormatter.format(amount).replace("₫", "VND");
    }
}
