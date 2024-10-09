package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetails, Long> {

    @Query(value = "SELECT \n" +
            "    o.order_id, \n" +
            "    p.product_name, \n" +
            "    pi.thumbnail AS product_image, \n" +
            "    c.category_name, \n" +
            "    CONCAT(IFNULL(s.size_name, ''), \n" +
            "           IFNULL(CONCAT(' - ', clr.color_name), '')) AS size_and_color, \n" +
            "    o.total_price AS order_total_price,\n" +
            "    od.price AS product_price, \n" +
            "    f.rating AS avg_rating, \n" +
            "    st.store_name, \n" +
            "    inv.total_amount, \n" +
            "    inv.shipping_fee, \n" +
            "    pay.payment_method, \n" +
            "    pay.payment_status, \n" +
            "    o.shipping_address, \n" +
            "    ss.status AS shipping_status, \n" +
            "    ss.tracking_number \n" +
            "FROM orders o\n" +
            "LEFT JOIN order_details od ON o.order_id = od.order_id\n" +
            "LEFT JOIN products p ON od.product_id = p.product_id\n" +
            "LEFT JOIN categories c ON p.category_id = c.category_id\n" +
            "LEFT JOIN product_variation pv ON od.variation_id = pv.variation_id\n" +
            "LEFT JOIN product_image pi ON pv.image_id = pi.image_id\n" +
            "LEFT JOIN size s ON pv.size_id = s.size_id\n" +
            "LEFT JOIN color clr ON pv.color_id = clr.color_id  -- Join the color table\n" +
            "LEFT JOIN stores st ON p.store_id = st.store_id\n" +
            "LEFT JOIN feedback f ON pv.variation_id = f.variation_id\n" +
            "LEFT JOIN invoice inv ON o.order_id = inv.order_id\n" +
            "LEFT JOIN payment pay ON o.order_id = pay.order_id\n" +
            "LEFT JOIN shipping_status ss ON o.order_id = ss.order_id\n" +
            "WHERE o.order_id = od.order_id;",
            nativeQuery = true)
    List<Object[]> getOrderDetails(@Param("orderId") Long orderId);
}
