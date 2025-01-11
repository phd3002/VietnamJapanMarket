package com.ecommerce.g58.repository.specification;

import com.ecommerce.g58.entity.Products;
import com.ecommerce.g58.entity.Feedback;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Subquery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder;

public class ProductSpecification {

    // Điều kiện để chỉ lấy sản phẩm có visible = true
    public static Specification<Products> isVisible() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("visible"));
    }

    // Điều kiện lọc theo categoryId
    public static Specification<Products> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("categoryId"), categoryId);
        };
    }

    // Điều kiện tìm kiếm theo tên sản phẩm
    public static Specification<Products> hasProductNameLike(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + search.toLowerCase() + "%");
        };
    }

    // Điều kiện lọc theo mức giá
    public static Specification<Products> hasPriceBetween(Integer minPrice, Integer maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            } else if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else { // maxPrice != null
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
        };
    }

    // Điều kiện lọc theo đánh giá trung bình sử dụng subquery
    public static Specification<Products> hasAverageRatingGreaterThanOrEqual(Double minRating) {
        return (root, query, criteriaBuilder) -> {
            if (minRating == null) {
                return criteriaBuilder.conjunction();
            }

            // Đảm bảo rằng query chỉ trả về kết quả duy nhất nếu sử dụng GROUP BY hoặc subquery
            // query.distinct(true); // Không cần thiết khi sử dụng subquery

            // Tạo subquery để tính trung bình đánh giá
            Subquery<Double> avgRatingSubquery = query.subquery(Double.class);
            Root<Feedback> feedbackRoot = avgRatingSubquery.from(Feedback.class);
            avgRatingSubquery.select(criteriaBuilder.avg(feedbackRoot.get("rating")));

            // Điều kiện liên kết Feedback qua ProductVariation tới Products
            // feedbackRoot.get("variationId") là ProductVariation
            // feedbackRoot.get("variationId").get("productId") là Products
            // feedbackRoot.get("variationId").get("productId").get("productId") là Integer (ID sản phẩm)
            avgRatingSubquery.where(
                    criteriaBuilder.equal(
                            feedbackRoot.get("variationId").get("productId").get("productId"),
                            root.get("productId")
                    )
            );

            // Thêm điều kiện trung bình đánh giá >= minRating
            return criteriaBuilder.ge(avgRatingSubquery, minRating);
        };
    }
}
