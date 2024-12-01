package com.ecommerce.g58.entity;

import com.ecommerce.g58.enums.PaymentMethod;
import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.ecommerce.g58.utils.FormatVND.formatCurrency;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users userId;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Column(name = "phone_number")
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private ShippingUnit unitId;

    @OneToMany(mappedBy = "orderId", fetch = FetchType.LAZY)
    private List<OrderDetails> orderDetails;

    @OneToMany(mappedBy = "orderId", fetch = FetchType.LAZY)
    private List<ShippingStatus> shippingStatus;

    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "order_code")
    private String orderCode;

    public String getPriceFormated() {
        return formatCurrency(BigDecimal.valueOf(totalPrice));
    }
}
