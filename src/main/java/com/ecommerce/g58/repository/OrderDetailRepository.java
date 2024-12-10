package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.OrderDetails;
import com.ecommerce.g58.entity.ProductVariation;
import com.ecommerce.g58.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetails, Long> {
    @Query("SELECT od FROM OrderDetails od WHERE od.orderId.orderId = :orderId")
    List<OrderDetails> findByOrderId(@Param("orderId") Integer orderId);

    @Query(value = "SELECT DISTINCT \n" +
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
            "    AVG(f.rating) AS avg_rating, \n" +
            "    st.store_name, \n" +
            "    st.picture_url, \n" +
            "    inv.total_amount, \n" +
            "    inv.shipping_fee, \n" +
            "    pay.payment_method, \n" +
            "    pay.payment_status,\n" +
            "    o.shipping_address, \n" +
            "    latest_status.status AS shipping_status,\n" +
            "    latest_status.previous_status AS previous_status,\n" +
            "    o.order_code, \n" +
            "    status_times.pending_time AS pending_time,\n" +
            "    status_times.confirmed_time AS confirmed_time,\n" +
            "    status_times.processing_time AS processing_time,\n" +
            "    status_times.dispatched_time AS dispatched_time,\n" +
            "    status_times.shipping_time AS shipping_time,\n" +
            "    status_times.failed_time AS failed_time,\n" +
            "    status_times.delivered_time AS delivered_time,\n" +
            "    status_times.completed_time AS completed_time,\n" +
            "    status_times.cancelled_time AS cancelled_time,\n" +
            "    status_times.returned_time AS returned_time, \n" +
            "    pv.variation_id\n" +
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
            "        ss1.previous_status,\n" +
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
            "        MAX(CASE WHEN status = 'Pending' THEN updated_at END) as pending_time,\n" +
            "        MAX(CASE WHEN status = 'Confirmed' THEN updated_at END) as confirmed_time,\n" +
            "        MAX(CASE WHEN status = 'Processing' THEN updated_at END) as processing_time,\n" +
            "        MAX(CASE WHEN status = 'Dispatched' THEN updated_at END) as dispatched_time,\n" +
            "        MAX(CASE WHEN status = 'Shipping' THEN updated_at END) as shipping_time,\n" +
            "        MAX(CASE WHEN status = 'Failed' THEN updated_at END) as failed_time,\n" +
            "        MAX(CASE WHEN status = 'Delivered' THEN updated_at END) as delivered_time,\n" +
            "        MAX(CASE WHEN status = 'Completed' THEN updated_at END) as completed_time,\n" +
            "        MAX(CASE WHEN status = 'Cancelled' THEN updated_at END) as cancelled_time,\n" +
            "        MAX(CASE WHEN status = 'Returned' THEN updated_at END) as returned_time\n" +
            "    FROM shipping_status\n" +
            "    GROUP BY order_id\n" +
            ") status_times ON o.order_id = status_times.order_id\n" +
            "WHERE o.order_id = :orderId\n" +
            "GROUP BY o.order_id, p.product_id, p.store_id, p.product_name, pi.thumbnail, c.category_name, s.size_name, clr.color_name, od.price, od.quantity, st.store_name, st.picture_url, inv.total_amount, inv.shipping_fee, pay.payment_method, pay.payment_status, o.shipping_address, latest_status.status, latest_status.previous_status, o.order_code, status_times.pending_time, status_times.confirmed_time, status_times.processing_time, status_times.dispatched_time, status_times.shipping_time, status_times.failed_time, status_times.delivered_time, status_times.completed_time, status_times.cancelled_time, status_times.returned_time, pv.variation_id",
            nativeQuery = true)
    List<Object[]> getOrderDetails(@Param("orderId") Long orderId);

    void deleteByProductId(Products productId);
    void deleteByVariationId(ProductVariation variationId);

    @Query(value = "WITH order_shipping AS (\n" +
            "    SELECT order_id, status, tracking_number, updated_at\n" +
            "    FROM shipping_status\n" +
            "    WHERE (order_id, updated_at) IN (\n" +
            "        SELECT order_id, MAX(updated_at)\n" +
            "        FROM shipping_status\n" +
            "        GROUP BY order_id\n" +
            "    )\n" +
            "),\n" +
            "order_total AS (\n" +
            "    SELECT \n" +
            "        o.order_id,\n" +
            "        SUM(od.quantity * od.price) as subtotal,\n" +
            "        iv.shipping_fee,\n" +
            "        iv.tax,\n" +
            "        SUM(od.quantity * od.price + (iv.tax)) + iv.shipping_fee as total_price\n" +
            "    FROM orders o\n" +
            "    LEFT JOIN order_details od ON o.order_id = od.order_id\n" +
            "    LEFT JOIN invoice iv ON iv.order_id = o.order_id\n" +
            "    GROUP BY o.order_id, iv.shipping_fee, iv.tax\n" +
            ")\n" +
            "\n" +
            "SELECT\n" +
            "    s.store_name,\n" +
            "    s.store_address,\n" +
            "    s.store_phone,\n" +
            "    os.status AS shipping_status,\n" +
            "    o.order_code,\n" +
            "    os.updated_at AS update_at,\n" +
            "    CONCAT(u.first_name, ' ', u.last_name) AS full_name,\n" +
            "    o.shipping_address,\n" +
            "    u.phone_number,\n" +
            "    o.order_date,\n" +
            "    p.product_name,\n" +
            "    CONCAT(c.color_name, ' x ', sz.size_name) AS product_type,\n" +
            "    od.quantity,\n" +
            "    od.price,\n" +
            "    (od.quantity * od.price) AS product_price,\n" +
            "    iv.shipping_fee,\n" +
            "    iv.tax,\n" +
            "    o.payment_method,\n" +
            "    ot.total_price AS total_order_price\n" +
            "FROM orders o\n" +
            "LEFT JOIN users u ON o.user_id = u.user_id\n" +
            "LEFT JOIN order_details od ON o.order_id = od.order_id\n" +
            "LEFT JOIN products p ON od.product_id = p.product_id\n" +
            "LEFT JOIN product_variation pv ON od.variation_id = pv.variation_id\n" +
            "LEFT JOIN color c ON c.color_id = pv.color_id\n" +
            "LEFT JOIN size sz ON sz.size_id = pv.size_id\n" +
            "LEFT JOIN stores s ON p.store_id = s.store_id\n" +
            "LEFT JOIN invoice iv ON iv.order_id = o.order_id\n" +
//            "LEFT JOIN payment pm ON pm.order_id = o.order_id\n" +
            "LEFT JOIN order_shipping os ON os.order_id = o.order_id\n" +
            "LEFT JOIN order_total ot ON ot.order_id = o.order_id\n" +
            "WHERE o.order_id = :orderId",
            nativeQuery = true)
    List<Object[]> getOrderDetailsByOrderId(@Param("orderId") Long orderId);
}
