package com.ecommerce.g58.repository;

import com.ecommerce.g58.entity.Stores;
import com.ecommerce.g58.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Stores, Integer> {
    long count();

    Optional<Stores> findByStoreName(String storeName);

    Page<Stores> findByStoreNameContainingIgnoreCase(String storeName, Pageable pageable);



    Optional<Stores> findByOwnerId(Users ownerId);

    @Query(value = "SELECT COALESCE(SUM(od.price * od.quantity), 0)\n" +
            "        FROM stores s\n" +
            "        JOIN products p ON s.store_id = p.store_id\n" +
            "        JOIN order_details od ON p.product_id = od.product_id\n" +
            "        JOIN orders o ON od.order_id = o.order_id\n" +
            "        JOIN shipping_status ss ON o.order_id = ss.order_id\n" +
            "        WHERE s.user_id = :userId\n" +
            "        AND ss.status = 'complete'\n" +
            "        AND (\n" +
            "            (:startDate IS NULL AND :endDate IS NULL\n" +
            "             AND o.order_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "             AND o.order_date <= LAST_DAY(CURRENT_DATE))\n" +
            "            \n" +
            "            OR\n" +
            "            (:startDate IS NOT NULL AND :endDate IS NULL\n" +
            "             AND o.order_date >= :startDate)\n" +
            "\n" +
            "            OR\n" +
            "            (:startDate IS NULL AND :endDate IS NOT NULL\n" +
            "             AND o.order_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "             AND o.order_date <= :endDate)\n" +
            "            \n" +
            "            OR\n" +
            "            (:startDate IS NOT NULL AND :endDate IS NOT NULL\n" +
            "             AND o.order_date >= :startDate\n" +
            "             AND o.order_date <= :endDate)\n" +
            "        )",
            nativeQuery = true)
    Integer calculateTotalRevenue(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query(value = "SELECT \n" +
            "    COALESCE(SUM(od.quantity), 0)\n" +
            "FROM \n" +
            "    stores s\n" +
            "JOIN \n" +
            "    products p ON s.store_id = p.store_id\n" +
            "JOIN \n" +
            "    order_details od ON p.product_id = od.product_id\n" +
            "JOIN \n" +
            "    orders o ON od.order_id = o.order_id\n" +
            "WHERE \n" +
            "    s.user_id = :userId\n" +
            "    AND (\n" +
            "        (:startDate IS NULL AND :endDate IS NULL\n" +
            "         AND o.order_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND o.order_date <= LAST_DAY(CURRENT_DATE))\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NULL\n" +
            "         AND o.order_date >= :startDate)\n" +
            "        OR\n" +
            "        (:startDate IS NULL AND :endDate IS NOT NULL\n" +
            "         AND o.order_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND o.order_date <= :endDate)\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NOT NULL\n" +
            "         AND o.order_date >= :startDate\n" +
            "         AND o.order_date <= :endDate)\n" +
            "    )",
            nativeQuery = true)
    Integer totalProductSold(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query(value = "SELECT COALESCE(COUNT(DISTINCT o.order_id), 0)\n" +
            "        FROM stores s\n" +
            "        JOIN products p ON s.store_id = p.store_id\n" +
            "        JOIN order_details od ON p.product_id = od.product_id\n" +
            "        JOIN orders o ON od.order_id = o.order_id\n" +
            "        WHERE s.user_id = :userId\n" +
            "        AND (\n" +
            "            (:startDate IS NULL AND :endDate IS NULL\n" +
            "             AND o.order_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "             AND o.order_date <= LAST_DAY(CURRENT_DATE))\n" +
            "            \n" +
            "            OR\n" +
            "            (:startDate IS NOT NULL AND :endDate IS NULL\n" +
            "             AND o.order_date >= :startDate)\n" +
            "\n" +
            "            OR\n" +
            "            (:startDate IS NULL AND :endDate IS NOT NULL\n" +
            "             AND o.order_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "             AND o.order_date <= :endDate)\n" +
            "            \n" +
            "            OR\n" +
            "            (:startDate IS NOT NULL AND :endDate IS NOT NULL\n" +
            "             AND o.order_date >= :startDate\n" +
            "             AND o.order_date <= :endDate)\n" +
            "        )",
            nativeQuery = true)
    Integer totalOrder(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query(value = "SELECT COALESCE(COUNT(DISTINCT o.order_id), 0)\n" +
            "        FROM stores s\n" +
            "        JOIN products p ON s.store_id = p.store_id\n" +
            "        JOIN order_details od ON p.product_id = od.product_id\n" +
            "        JOIN orders o ON od.order_id = o.order_id\n" +
            "        JOIN shipping_status ss ON o.order_id = ss.order_id\n" +
            "        WHERE s.user_id = :userId\n" +
            "        AND ss.status = 'complete'\n" +
            "        AND (\n" +
            "            (:startDate IS NULL AND :endDate IS NULL\n" +
            "             AND o.order_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "             AND o.order_date <= LAST_DAY(CURRENT_DATE))\n" +
            "            \n" +
            "            OR\n" +
            "            (:startDate IS NOT NULL AND :endDate IS NULL\n" +
            "             AND o.order_date >= :startDate)\n" +
            "\n" +
            "            OR\n" +
            "            (:startDate IS NULL AND :endDate IS NOT NULL\n" +
            "             AND o.order_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "             AND o.order_date <= :endDate)\n" +
            "            \n" +
            "            OR\n" +
            "            (:startDate IS NOT NULL AND :endDate IS NOT NULL\n" +
            "             AND o.order_date >= :startDate\n" +
            "             AND o.order_date <= :endDate)\n" +
            "        )",
            nativeQuery = true)
    Integer totalOrderComplete(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query(value = "SELECT COALESCE(COUNT(DISTINCT o.order_id), 0)\n" +
            "    FROM stores s\n" +
            "    JOIN products p ON s.store_id = p.store_id\n" +
            "    JOIN order_details od ON p.product_id = od.product_id\n" +
            "    JOIN orders o ON od.order_id = o.order_id\n" +
            "    JOIN shipping_status ss ON o.order_id = ss.order_id\n" +
            "    WHERE s.user_id = :userId\n" +
            "    AND ss.status = 'returned' AND ss.status = 'cancelled'\n" +
            "    AND (\n" +
            "        (:startDate IS NULL AND :endDate IS NULL\n" +
            "         AND o.order_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND o.order_date <= LAST_DAY(CURRENT_DATE))\n" +
            "        \n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NULL\n" +
            "         AND o.order_date >= :startDate)\n" +
            "\n" +
            "        OR\n" +
            "        (:startDate IS NULL AND :endDate IS NOT NULL\n" +
            "         AND o.order_date >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND o.order_date <= :endDate)\n" +
            "        \n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NOT NULL\n" +
            "         AND o.order_date >= :startDate\n" +
            "         AND o.order_date <= :endDate)\n" +
            "    )",
            nativeQuery = true)
    Integer totalOrderCancelledAndReturned(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query(value = "SELECT \n" +
            "        p.product_code,\n" +
            "        p.product_name,\n" +
            "        p.price,\n" +
            "        c.category_name,\n" +
            "        SUM(od.quantity) AS total_sold\n" +
            "    FROM \n" +
            "        products p\n" +
            "    JOIN \n" +
            "        order_details od ON p.product_id = od.product_id\n" +
            "    JOIN \n" +
            "        orders o ON od.order_id = o.order_id\n" +
            "    JOIN \n" +
            "        shipping_status ss ON o.order_id = ss.order_id\n" +
            "    JOIN \n" +
            "        categories c ON p.category_id = c.category_id\n" +
            "        JOIN stores s ON s.store_id = p.store_id\n" +
            "    WHERE \n" +
            "        s.user_id = :userId \n" +
            "        AND ss.status = 'Complete'\n" +
            "        AND (\n" +
            "            (:startDate IS NULL AND :endDate IS NULL\n" +
            "             AND o.order_date >= DATE_FORMAT(CURDATE(), '%Y-%m-01')\n" +
            "             AND o.order_date <= LAST_DAY(CURDATE()))\n" +
            "            OR\n" +
            "            (:startDate IS NOT NULL AND :endDate IS NULL\n" +
            "             AND o.order_date >= :startDate)\n" +
            "            OR\n" +
            "            (:startDate IS NULL AND :endDate IS NOT NULL\n" +
            "             AND o.order_date <= :endDate\n" +
            "             AND o.order_date >= DATE_FORMAT(CURDATE(), '%Y-%m-01'))\n" +
            "            OR\n" +
            "            (:startDate IS NOT NULL AND :endDate IS NOT NULL\n" +
            "             AND o.order_date >= :startDate\n" +
            "             AND o.order_date <= :endDate)\n" +
            "        )\n" +
            "    GROUP BY \n" +
            "        p.product_code, p.product_name, p.price, c.category_name\n" +
            "    ORDER BY \n" +
            "        total_sold DESC\n" +
            "        LIMIT 10;",
            nativeQuery = true)
    List<Object[]> bestSelling(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query(value = "SELECT \n" +
            "    COALESCE(COUNT(f.feedback_id), 0)\n" +
            "FROM stores s\n" +
            "LEFT JOIN feedback f ON s.store_id = f.store_id\n" +
            "    AND f.rating = 5\n" +
            "    AND (\n" +
            "        (:startDate IS NULL AND :endDate IS NULL\n" +
            "         AND f.created_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND f.created_at <= LAST_DAY(CURRENT_DATE))\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NULL\n" +
            "         AND f.created_at >= :startDate)\n" +
            "        OR\n" +
            "        (:startDate IS NULL AND :endDate IS NOT NULL\n" +
            "         AND f.created_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND f.created_at <= :endDate)\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NOT NULL\n" +
            "         AND f.created_at >= :startDate\n" +
            "         AND f.created_at <= :endDate)\n" +
            "    )\n" +
            "WHERE s.user_id = :userId\n" +
            "GROUP BY s.store_id",
            nativeQuery = true)
    Integer countRatings5Star(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query(value = "SELECT \n" +
            "    COALESCE(COUNT(f.feedback_id), 0)\n" +
            "FROM stores s\n" +
            "LEFT JOIN feedback f ON s.store_id = f.store_id\n" +
            "    AND f.rating = 4\n" +
            "    AND (\n" +
            "        (:startDate IS NULL AND :endDate IS NULL\n" +
            "         AND f.created_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND f.created_at <= LAST_DAY(CURRENT_DATE))\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NULL\n" +
            "         AND f.created_at >= :startDate)\n" +
            "        OR\n" +
            "        (:startDate IS NULL AND :endDate IS NOT NULL\n" +
            "         AND f.created_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND f.created_at <= :endDate)\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NOT NULL\n" +
            "         AND f.created_at >= :startDate\n" +
            "         AND f.created_at <= :endDate)\n" +
            "    )\n" +
            "WHERE s.user_id = :userId\n" +
            "GROUP BY s.store_id",
            nativeQuery = true)
    Integer countRatings4Star(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query(value = "SELECT \n" +
            "    COALESCE(COUNT(f.feedback_id), 0)\n" +
            "FROM stores s\n" +
            "LEFT JOIN feedback f ON s.store_id = f.store_id\n" +
            "    AND f.rating = 3\n" +
            "    AND (\n" +
            "        (:startDate IS NULL AND :endDate IS NULL\n" +
            "         AND f.created_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND f.created_at <= LAST_DAY(CURRENT_DATE))\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NULL\n" +
            "         AND f.created_at >= :startDate)\n" +
            "        OR\n" +
            "        (:startDate IS NULL AND :endDate IS NOT NULL\n" +
            "         AND f.created_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND f.created_at <= :endDate)\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NOT NULL\n" +
            "         AND f.created_at >= :startDate\n" +
            "         AND f.created_at <= :endDate)\n" +
            "    )\n" +
            "WHERE s.user_id = :userId\n" +
            "GROUP BY s.store_id",
            nativeQuery = true)
    Integer countRatings3Star(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query(value = "SELECT \n" +
            "    COALESCE(COUNT(f.feedback_id), 0)\n" +
            "FROM stores s\n" +
            "LEFT JOIN feedback f ON s.store_id = f.store_id\n" +
            "    AND f.rating = 2\n" +
            "    AND (\n" +
            "        (:startDate IS NULL AND :endDate IS NULL\n" +
            "         AND f.created_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND f.created_at <= LAST_DAY(CURRENT_DATE))\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NULL\n" +
            "         AND f.created_at >= :startDate)\n" +
            "        OR\n" +
            "        (:startDate IS NULL AND :endDate IS NOT NULL\n" +
            "         AND f.created_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND f.created_at <= :endDate)\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NOT NULL\n" +
            "         AND f.created_at >= :startDate\n" +
            "         AND f.created_at <= :endDate)\n" +
            "    )\n" +
            "WHERE s.user_id = :userId\n" +
            "GROUP BY s.store_id",
            nativeQuery = true)
    Integer countRatings2Star(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query(value = "SELECT \n" +
            "    COALESCE(COUNT(f.feedback_id), 0)\n" +
            "FROM stores s\n" +
            "LEFT JOIN feedback f ON s.store_id = f.store_id\n" +
            "    AND f.rating = 1\n" +
            "    AND (\n" +
            "        (:startDate IS NULL AND :endDate IS NULL\n" +
            "         AND f.created_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND f.created_at <= LAST_DAY(CURRENT_DATE))\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NULL\n" +
            "         AND f.created_at >= :startDate)\n" +
            "        OR\n" +
            "        (:startDate IS NULL AND :endDate IS NOT NULL\n" +
            "         AND f.created_at >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01')\n" +
            "         AND f.created_at <= :endDate)\n" +
            "        OR\n" +
            "        (:startDate IS NOT NULL AND :endDate IS NOT NULL\n" +
            "         AND f.created_at >= :startDate\n" +
            "         AND f.created_at <= :endDate)\n" +
            "    )\n" +
            "WHERE s.user_id = :userId\n" +
            "GROUP BY s.store_id",
            nativeQuery = true)
    Integer countRatings1Star(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    @Query(value = "SELECT COALESCE(SUM(od.price * od.quantity), 0)\n" +
            "FROM stores s\n" +
            "JOIN products p ON s.store_id = p.store_id\n" +
            "JOIN order_details od ON p.product_id = od.product_id\n" +
            "JOIN orders o ON od.order_id = o.order_id\n" +
            "JOIN shipping_status ss ON o.order_id = ss.order_id\n" +
            "WHERE s.user_id = :userId\n" +
            "AND ss.status = 'complete'\n" +
            "    AND (MONTH(o.order_date) = MONTH(CURRENT_DATE)\n" +
            "    AND YEAR(o.order_date) = YEAR(CURRENT_DATE))",
            nativeQuery = true)
    Integer totalRevenueCurrent(@Param("userId") Integer userId);

    @Query(value = "SELECT COALESCE(SUM(od.price * od.quantity), 0)\n" +
            "FROM stores s\n" +
            "JOIN products p ON s.store_id = p.store_id\n" +
            "JOIN order_details od ON p.product_id = od.product_id\n" +
            "JOIN orders o ON od.order_id = o.order_id\n" +
            "JOIN shipping_status ss ON o.order_id = ss.order_id\n" +
            "WHERE s.user_id = :userId\n" +
            "AND ss.status = 'complete'\n" +
            "    AND (o.order_date >= DATE_SUB(DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), INTERVAL 1 MONTH)\n" +
            "    AND o.order_date < DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'))",
            nativeQuery = true)
    Integer totalRevenueLastMonth(@Param("userId") Integer userId);

    @Query(value = "SELECT COALESCE(SUM(od.price * od.quantity), 0)\n" +
            "FROM stores s\n" +
            "JOIN products p ON s.store_id = p.store_id\n" +
            "JOIN order_details od ON p.product_id = od.product_id\n" +
            "JOIN orders o ON od.order_id = o.order_id\n" +
            "JOIN shipping_status ss ON o.order_id = ss.order_id\n" +
            "WHERE s.user_id = :userId\n" +
            "AND ss.status = 'complete'\n" +
            "    AND (o.order_date >= DATE_SUB(DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), INTERVAL 2 MONTH)\n" +
            "    AND o.order_date < DATE_SUB(DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), INTERVAL 1 MONTH))",
            nativeQuery = true)
    Integer totalRevenueLast2Months(@Param("userId") Integer userId);

    @Query(value = "SELECT COALESCE(SUM(od.price * od.quantity), 0)\n" +
            "FROM stores s\n" +
            "JOIN products p ON s.store_id = p.store_id\n" +
            "JOIN order_details od ON p.product_id = od.product_id\n" +
            "JOIN orders o ON od.order_id = o.order_id\n" +
            "JOIN shipping_status ss ON o.order_id = ss.order_id\n" +
            "WHERE s.user_id = :userId\n" +
            "AND ss.status = 'complete'\n" +
            "    AND (o.order_date >= DATE_SUB(DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), INTERVAL 3 MONTH)\n" +
            "    AND o.order_date < DATE_SUB(DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), INTERVAL 2 MONTH))",
            nativeQuery = true)
    Integer totalRevenueLast3Months(@Param("userId") Integer userId);

    @Query(value = "SELECT COALESCE(SUM(od.price * od.quantity), 0)\n" +
            "FROM stores s\n" +
            "JOIN products p ON s.store_id = p.store_id\n" +
            "JOIN order_details od ON p.product_id = od.product_id\n" +
            "JOIN orders o ON od.order_id = o.order_id\n" +
            "JOIN shipping_status ss ON o.order_id = ss.order_id\n" +
            "WHERE s.user_id = :userId\n" +
            "AND ss.status = 'complete'\n" +
            "    AND (o.order_date >= DATE_SUB(DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), INTERVAL 4 MONTH)\n" +
            "    AND o.order_date < DATE_SUB(DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), INTERVAL 3 MONTH))",
            nativeQuery = true)
    Integer totalRevenueLast4Months(@Param("userId") Integer userId);

    @Query(value = "SELECT COALESCE(SUM(od.price * od.quantity), 0)\n" +
            "FROM stores s\n" +
            "JOIN products p ON s.store_id = p.store_id\n" +
            "JOIN order_details od ON p.product_id = od.product_id\n" +
            "JOIN orders o ON od.order_id = o.order_id\n" +
            "JOIN shipping_status ss ON o.order_id = ss.order_id\n" +
            "WHERE s.user_id = :userId\n" +
            "AND ss.status = 'complete'\n" +
            "    AND (o.order_date >= DATE_SUB(DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), INTERVAL 5 MONTH)\n" +
            "    AND o.order_date < DATE_SUB(DATE_FORMAT(CURRENT_DATE, '%Y-%m-01'), INTERVAL 4 MONTH))",
            nativeQuery = true)
    Integer totalRevenueLast5Months(@Param("userId") Integer userId);
}
