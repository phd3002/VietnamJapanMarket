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
            "    p.product_id, \n" +
            "    p.store_id, \n" +
            "    p.product_name, \n" +
            "    pi.thumbnail AS product_image, \n" +
            "    c.category_name, \n" +
            "    CONCAT(IFNULL(s.size_name, ''), \n" +
            "           IFNULL(CONCAT(' - ', clr.color_name), '')) AS size_and_color, \n" +
            "    (od.price * od.quantity) AS order_total_price,\n" +
            "    od.quantity,\n" +
            "    od.price AS product_price, \n" +
            "    f.rating AS avg_rating, \n" +
            "    st.store_name, \n" +
            "    st.store_image, \n" +
            "    inv.total_amount, \n" +
            "    inv.shipping_fee, \n" +
            "    pay.payment_method, \n" +
            "    pay.payment_status,\n" +
            "    o.shipping_address, \n" +
            "    latest_status.status AS shipping_status,\n" +
            "    latest_status.tracking_number,\n" +
            "    (status_times.order_placed_time) AS order_placed_time,\n" +
            "    (status_times.payment_time) AS payment_time,\n" +
            "    (status_times.shipping_time) AS shipping_time,\n" +
            "    (status_times.delivered_time) AS delivered_time,\n" +
            "    (status_times.completed_time) AS completed_time\n" +
            "FROM orders o\n" +
            "LEFT JOIN order_details od ON o.order_id = od.order_id\n" +
            "LEFT JOIN products p ON od.product_id = p.product_id\n" +
            "LEFT JOIN categories c ON p.category_id = c.category_id\n" +
            "LEFT JOIN product_variation pv ON od.variation_id = pv.variation_id\n" +
            "LEFT JOIN product_image pi ON pv.image_id = pi.image_id\n" +
            "LEFT JOIN size s ON pv.size_id = s.size_id\n" +
            "LEFT JOIN color clr ON pv.color_id = clr.color_id \n" +
            "LEFT JOIN stores st ON p.store_id = st.store_id\n" +
            "LEFT JOIN feedback f ON pv.variation_id = f.variation_id\n" +
            "LEFT JOIN invoice inv ON o.order_id = inv.order_id\n" +
            "LEFT JOIN payment pay ON o.order_id = pay.order_id\n" +
            "LEFT JOIN (\n" +
            "    SELECT \n" +
            "        ss1.order_id,\n" +
            "        ss1.status,\n" +
            "        ss1.tracking_number,\n" +
            "        ss1.updated_at\n" +
            "    FROM shipping_status ss1\n" +
            "    LEFT JOIN shipping_status ss2\n" +
            "        ON ss1.order_id = ss2.order_id \n" +
            "        AND ss1.updated_at < ss2.updated_at\n" +
            "    WHERE ss2.status_id IS NULL\n" +
            ") latest_status ON o.order_id = latest_status.order_id\n" +
            "LEFT JOIN (\n" +
            "    SELECT \n" +
            "        order_id,\n" +
            "        MAX(CASE WHEN status = 'Order Placed' THEN updated_at END) as order_placed_time,\n" +
            "        MAX(CASE WHEN status = 'Paid' THEN updated_at END) as payment_time,\n" +
            "        MAX(CASE WHEN status = 'Shipping' THEN updated_at END) as shipping_time,\n" +
            "        MAX(CASE WHEN status = 'Delivered' THEN updated_at END) as delivered_time,\n" +
            "        MAX(CASE WHEN status = 'Order Completed' THEN updated_at END) as completed_time\n" +
            "    FROM shipping_status\n" +
            "    GROUP BY order_id\n" +
            ") status_times ON o.order_id = status_times.order_id\n" +
            "WHERE o.order_id = :orderId",
            nativeQuery = true)
    List<Object[]> getOrderDetails(@Param("orderId") Long orderId);
}
