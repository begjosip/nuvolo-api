package com.nuvolo.nuvoloapi.service;

import com.nuvolo.nuvoloapi.exceptions.ProductException;
import com.nuvolo.nuvoloapi.minio.MinioService;
import com.nuvolo.nuvoloapi.model.dto.request.ProductRequestDto;
import com.nuvolo.nuvoloapi.model.dto.response.ProductResponseDto;
import com.nuvolo.nuvoloapi.model.entity.*;
import com.nuvolo.nuvoloapi.repository.ProductImageRepository;
import com.nuvolo.nuvoloapi.repository.ProductInventoryRepository;
import com.nuvolo.nuvoloapi.repository.ProductRepository;
import com.nuvolo.nuvoloapi.repository.TypeRepository;
import com.nuvolo.nuvoloapi.util.FileUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final CategoryService categoryService;

    private final MinioService minioService;

    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

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

        List<ProductImage> savedProductImages = this.saveProductImages(savedProduct, images);
        minioService.uploadProductImages(savedProductImages, images);
    }

    private List<ProductImage> saveProductImages(Product product, MultipartFile[] images) {
        log.debug("Saving product images to database.");
        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile image : images) {
            ProductImage imageEntity = ProductImage.builder()
                    .imageNo(UUID.randomUUID().toString())
                    .extension(FilenameUtils.getExtension(image.getOriginalFilename()))
                    .product(product)
                    .build();
            productImages.add(imageEntity);
        }
        return productImageRepository.saveAll(productImages);
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
        minioService.deleteProductImages(productImageRepository.findAllByProductId(id));
        log.debug("Successfully deleted product images.");
    }


    @Transactional
    public Page<ProductResponseDto> getProducts(Integer pageNo, Integer pageSize) {
        log.debug("Fetching products page from database.");
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> productsPage = productRepository.findAllByOrderByCreatedAtDesc(pageable);
        return productsPage.map(product -> {
            List<ProductImage> productImages = productImageRepository.findAllByProductId(product.getId());
            List<String> imagesUrls = productImages.stream()
                    .map(productImage ->
                            minioService.getImageUrl(productImage.getImageNo().concat(".").concat(productImage.getExtension()))
                    ).toList();
            return ProductResponseDto.mapProductEntity(product, imagesUrls);
        });
    }

    @Transactional
    public Product findProductById(Long id) {
        log.debug("Finding product with ID: {}", id);
        return productRepository.findById(id).orElseThrow(() -> new ProductException("Can not find product with given ID."));
    }

    @Transactional
    public ProductResponseDto getProductWithId(Long id) {
        log.debug("Fetching product with ID: {}", id);
        List<String> imagesUrls = productImageRepository.findAllByProductId(id)
                .stream().map(productImage ->
                        minioService.getImageUrl(productImage.getImageNo().concat(".").concat(productImage.getExtension()))
                ).toList();
        return ProductResponseDto.mapProductEntity(findProductById(id), imagesUrls);
    }
}
