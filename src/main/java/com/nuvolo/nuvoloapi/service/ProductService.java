package com.nuvolo.nuvoloapi.service;

import com.nuvolo.nuvoloapi.exceptions.ProductException;
import com.nuvolo.nuvoloapi.model.dto.request.ProductRequestDto;
import com.nuvolo.nuvoloapi.model.entity.Category;
import com.nuvolo.nuvoloapi.model.entity.Product;
import com.nuvolo.nuvoloapi.model.entity.ProductInventory;
import com.nuvolo.nuvoloapi.model.entity.Type;
import com.nuvolo.nuvoloapi.repository.ProductInventoryRepository;
import com.nuvolo.nuvoloapi.repository.ProductRepository;
import com.nuvolo.nuvoloapi.repository.TypeRepository;
import com.nuvolo.nuvoloapi.util.FileUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final CategoryService categoryService;

    private final ProductRepository productRepository;

    private final ProductInventoryRepository productInventoryRepository;

    private final TypeRepository typeRepository;

    @Transactional
    public void addProduct(ProductRequestDto productRequest, MultipartFile[] images) {
        this.validateProductRequest(productRequest, images);
        Type productType = typeRepository.findById(productRequest.getTypeId())
                .orElseThrow(() -> new ProductException(
                        String.format("Product type with %s does not exists.", productRequest.getTypeId().toString())
                ));
        Category category = categoryService.findCategoryById(productRequest.getCategoryId());
        ProductInventory productInventory = this.saveProductInventory(productRequest.getQuantity());

        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .type(productType)
                .category(category)
                .price(productRequest.getPrice().setScale(2, RoundingMode.HALF_UP))
                .productInventory(productInventory)
                .build();

        log.debug("Saving product entity to database.");
        Product savedProduct = productRepository.save(product);
        log.debug("Product with ID:{} saved to database.", savedProduct.getId());

        try {
            FileUtil.saveProductImages(savedProduct, images);
            log.debug("Successfully saved all product images.");
        } catch (Exception ex) {
            log.error("Error occurred while trying to save product images.");
            FileUtil.deleteProductImages(savedProduct.getId());
        }
    }

    private void validateProductRequest(ProductRequestDto productRequest, MultipartFile[] images) {
        log.debug("Validating product request.");
        if (productRequest.getQuantity() < 1) {
            throw new ProductException("Product quantity needs to be more than one.");
        }
        BigDecimal roundedPrice = productRequest.getPrice().setScale(2, RoundingMode.HALF_UP);
        if (roundedPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductException("Invalid product price. Price needs to more than 0.00.");
        }
        for (MultipartFile file : images) {
            if (file.isEmpty())
                throw new ProductException("No images attached for product.");
            if (!FileUtil.checkIfFileContentTypeIsValid(file))
                throw new ProductException("Invalid file content type.");
        }
        log.debug("Product request is validated.");
    }

    private ProductInventory saveProductInventory(Integer quantity) {
        log.debug("Saving product inventory");
        ProductInventory productInventory = productInventoryRepository.save(
                ProductInventory.builder()
                        .quantity(quantity)
                        .build()
        );
        log.debug("Saved product inventory with ID: {}", productInventory.getId());
        return productInventory;
    }

    @Transactional
    public void deleteProductWithId(Long id) {
        log.debug("Deleting product with ID: {}", id);
        productRepository.deleteById(id);
        log.debug("Successfully deleted product with ID: {}", id);
        FileUtil.deleteProductImages(id);
        log.debug("Successfully deleted product images.");
    }
}
