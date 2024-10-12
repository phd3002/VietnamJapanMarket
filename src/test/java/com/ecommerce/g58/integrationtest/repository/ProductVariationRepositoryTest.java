//package com.ecommerce.g58.integrationtest.repository;
//import com.ecommerce.g58.entity.Products;
//import com.ecommerce.g58.entity.ProductVariation;
//import com.ecommerce.g58.entity.Size;
//import com.ecommerce.g58.repository.ProductRepository;
//import com.ecommerce.g58.repository.ProductVariationRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.TestPropertySource;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@TestPropertySource(locations = "classpath:application-test.properties")
//public class ProductVariationRepositoryTest {
//    @Autowired
//    private ProductVariationRepository productVariationRepository;
//
//    @Autowired
//    private ProductRepository productRepository; // Giả sử bạn có một repository cho Product
//
//    private Products product;
//
//    private Size size;
//
//    @BeforeEach
//    void setUp() {
//        // Tạo và lưu một sản phẩm mẫu
//        product = Products.builder()
//                .productName("Phone")
//                .productDescription("This is a product.")
//                .price(100)
//                .weight(1.5f)
//                .productType("Electronics")
//                .build();
//        product = productRepository.save(product);
//
//        // Tạo và lưu một kích thước mẫu
//        size = Size.builder()
//                .sizeName("M") // Gig ả sử bạn có thuộc tính sizeName tronSize
//                .build();
//        size = sizeRepository.save(size);
//
//        // Tạo và lưu một màu sắc mẫu
//        color = Color.builder()
//                .colorName("Red") // Giả sử bạn có thuộc tính colorName trong Color
//                .build();
//        color = colorRepository.save(color);
//
//        // Tạo và lưu một hình ảnh sản phẩm mẫu
//        productImage = ProductImage.builder()
//                .imageUrl("http://example.com/image.jpg") // Giả sử bạn có thuộc tính imageUrl trong ProductImage
//                .build();
//        productImage = productImageRepository.save(productImage);
//
//        // Lưu các biến thể sản phẩm
//        ProductVariation variation1 = ProductVariation.builder()
//                .productId(product) // Thay thế bằng đối tượng sản phẩm đã lưu
//                .size(size) // Thay thế bằng kích thước đã lưu
//                .color(color) // Thay thế bằng màu sắc đã lưu
//                .stock(10)
//                .imageId(productImage) // Thay thế bằng hình ảnh đã lưu
//                .build();
//        productVariationRepository.save(variation1);
//
//        ProductVariation variation2 = ProductVariation.builder()
//                .productId(product) // Thay thế bằng đối tượng sản phẩm đã lưu
//                .size(size) // Thay thế bằng kích thước đã lưu
//                .color(color) // Thay thế bằng màu sắc đã lưu
//                .stock(5)
//                .imageId(productImage) // Thay thế bằng hình ảnh đã lưu
//                .build();
//        productVariationRepository.save(variation2);
//    }
//
//
//    @Test
//    void testFindByProductIdProductId() {
//        // Given
//        Integer productId = product.getId(); // Lấy ID của sản phẩm đã lưu
//
//        // When
//        List<ProductVariation> variations = productVariationRepository.findByProductIdProductId(productId);
//
//        // Then
//        assertThat(variations).isNotEmpty(); // Kiểm tra rằng có ít nhất một biến thể
//        assertThat(variations).hasSize(2); // Kiểm tra rằng có 2 biến thể
//        assertThat(variations.get(0).getColor()).isIn("Red", "Blue"); // Kiểm tra màu sắc
//        assertThat(variations.get(1).getColor()).isIn("Red", "Blue"); // Kiểm tra màu sắc
//    }
//
//    @Test
//    void testFindByProductIdProductIdNotFound() {
//        // Given
//        Integer nonExistentProductId = 999; // Một ID không tồn tại
//
//        // When
//        List<ProductVariation> variations = productVariationRepository.findByProductIdProductId(nonExistentProductId);
//
//        // Then
//        assertThat(variations).isEmpty(); // Kiểm tra rằng không tìm thấy biến thể nào
//    }
//}
//
