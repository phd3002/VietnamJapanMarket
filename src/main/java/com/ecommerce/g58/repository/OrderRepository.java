package com.ecommerce.g58.repository;

import com.ecommerce.g58.dto.OrderManagerDTO;
import com.ecommerce.g58.dto.OrdersDTO;
import com.ecommerce.g58.entity.Orders;
import com.ecommerce.g58.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    void deleteByUserId_UserId(Integer userId);

    @Query("SELECT o FROM Orders o WHERE o.userId.userId = :userId")
    List<Orders> findByUserId(Users user);

    // Add new methods to handle shipping address
    @Query("SELECT o.shippingAddress FROM Orders o WHERE o.orderId = :orderId")
    String findShippingAddressByOrderId(@Param("orderId") Integer orderId);

    @Modifying
    @Query("UPDATE Orders o SET o.shippingAddress = :newAddress WHERE o.orderId = :orderId")
    void updateShippingAddressByOrderId(@Param("orderId") Integer orderId, @Param("newAddress") String newAddress);

    @Query(value = "SELECT \n" +
            "    o.order_id AS orderId, \n" +
            "    o.order_date AS orderDate, \n" +
            "    latest_status.status AS status, \n" +
            "    COALESCE(SUM(od.quantity), 0) AS totalQuantity, \n" +
            "    COALESCE(o.total_price, 0) + COALESCE(i.shipping_fee, 0) AS totalPrice \n" +
            "FROM \n" +
            "    orders o \n" +
            "LEFT JOIN \n" +
            "    order_details od ON o.order_id = od.order_id \n" +
            "LEFT JOIN \n" +
            "    invoice i ON o.order_id = i.order_id \n" +
            "LEFT JOIN (\n" +
            "    SELECT ss.order_id, ss.status\n" +
            "    FROM shipping_status ss\n" +
            "    INNER JOIN (\n" +
            "        SELECT order_id, MAX(updated_at) AS latest_update\n" +
            "        FROM shipping_status\n" +
            "        GROUP BY order_id\n" +
            "    ) latest ON ss.order_id = latest.order_id AND ss.updated_at = latest.latest_update\n" +
            ") latest_status ON o.order_id = latest_status.order_id \n" +
            "WHERE o.user_id = :userId " +
            "AND (COALESCE(:status, '') = '' OR latest_status.status = :status)\n" +
            "GROUP BY \n" +
            "    o.order_id, o.order_date, latest_status.status, o.total_price, i.shipping_fee",
            countQuery = "SELECT COUNT(DISTINCT o.order_id) " +
                    "FROM orders o " +
                    "LEFT JOIN (\n" +
                    "    SELECT ss.order_id, ss.status\n" +
                    "    FROM shipping_status ss\n" +
                    "    INNER JOIN (\n" +
                    "        SELECT order_id, MAX(updated_at) AS latest_update\n" +
                    "        FROM shipping_status\n" +
                    "        GROUP BY order_id\n" +
                    "    ) latest ON ss.order_id = latest.order_id AND ss.updated_at = latest.latest_update\n" +
                    ") latest_status ON o.order_id = latest_status.order_id " +
                    "WHERE o.user_id = :userId " +
                    "AND (COALESCE(:status, '') = '' OR latest_status.status = :status)",
            nativeQuery = true)
    Page<Object[]> findOrdersByUserIdAndStatus(@Param("userId") Integer userId,
                                               @Param("status") String status,
                                               Pageable pageable);

    @Query(value = "SELECT \n" +
            "    o.order_id AS 'Mã đơn hàng',\n" +
            "    CONCAT(u.first_name, ' ', u.last_name) AS full_name,\n" +
            "    GROUP_CONCAT(DISTINCT p.product_name SEPARATOR ', ') AS product_names,\n" +
            "    (SELECT SUM(od_inner.quantity) \n" +
            "     FROM order_details od_inner \n" +
            "     WHERE od_inner.order_id = o.order_id) AS total_products, -- Total quantity of items in the order\n" +
            "    (o.total_price + COALESCE(MAX(i.shipping_fee), 0)) AS order_price,\n" +
            "    (SELECT status \n" +
            "     FROM shipping_status \n" +
            "     WHERE order_id = o.order_id\n" +
            "     ORDER BY updated_at DESC\n" +
            "     LIMIT 1) AS latest_status\n" +
            "FROM \n" +
            "    stores s\n" +
            "JOIN \n" +
            "    products p ON p.store_id = s.store_id\n" +
            "JOIN \n" +
            "    order_details od ON od.product_id = p.product_id\n" +
            "JOIN \n" +
            "    orders o ON o.order_id = od.order_id\n" +
            "JOIN \n" +
            "    users u ON u.user_id = o.user_id\n" +
            "LEFT JOIN \n" +
            "    invoice i ON i.order_id = o.order_id\n" +
            "LEFT JOIN \n" +
            "    shipping_status ss ON ss.order_id = o.order_id\n" +
            "WHERE \n" +
            "    s.user_id = :userId\n" +
            "GROUP BY \n" +
            "    o.order_id, u.first_name, u.last_name, o.total_price",
            nativeQuery = true)
    List<Object[]> findOrdersByStoreUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT \n" +
            "    o.order_id AS orderId, \n" +
            "    CONCAT(u.first_name, ' ', u.last_name) AS customerName, \n" +
            "    GROUP_CONCAT(DISTINCT p.product_name SEPARATOR ', ') AS productNames, \n" +
            "    SUM(od.quantity) AS totalProducts, \n" +
            "    (o.total_price + COALESCE(MAX(i.shipping_fee), 0)) AS order_price,\n" +
            "    (SELECT ss.status \n" +
            "     FROM shipping_status ss \n" +
            "     WHERE ss.order_id = o.order_id \n" +
            "     ORDER BY ss.updated_at DESC \n" +
            "     LIMIT 1) AS latestStatus \n" +
            "FROM \n" +
            "    orders o \n" +
            "JOIN \n" +
            "    users u ON u.user_id = o.user_id \n" +
            "JOIN \n" +
            "    order_details od ON od.order_id = o.order_id \n" +
            "JOIN \n" +
            "    products p ON p.product_id = od.product_id \n" +
            "JOIN \n" +
            "    stores s ON s.store_id = p.store_id \n" +
            "LEFT JOIN \n" +
            "    invoice i ON i.order_id = o.order_id \n" +
            "LEFT JOIN \n" +
            "    shipping_status ss ON ss.order_id = o.order_id \n" +
            "WHERE \n" +
            "    s.store_id = :storeId \n" +
            "GROUP BY \n" +
            "    o.order_id, u.first_name, u.last_name, o.total_price",
            nativeQuery = true)
    List<Object[]> findOrdersByStoreId(@Param("storeId") Integer storeId);
}
