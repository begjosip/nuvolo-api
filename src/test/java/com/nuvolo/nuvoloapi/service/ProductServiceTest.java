package com.nuvolo.nuvoloapi.service;

import com.nuvolo.nuvoloapi.exceptions.ProductException;
import com.nuvolo.nuvoloapi.minio.MinioService;
import com.nuvolo.nuvoloapi.model.dto.request.ProductRequestDto;
import com.nuvolo.nuvoloapi.model.dto.response.ProductResponseDto;
import com.nuvolo.nuvoloapi.model.entity.*;
import com.nuvolo.nuvoloapi.model.enums.ProductType;
import com.nuvolo.nuvoloapi.repository.ProductImageRepository;
import com.nuvolo.nuvoloapi.repository.ProductInventoryRepository;
import com.nuvolo.nuvoloapi.repository.ProductRepository;
import com.nuvolo.nuvoloapi.repository.TypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductInventoryRepository productInventoryRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private TypeRepository typeRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private ProductService productService;

    @Captor
    private ArgumentCaptor<ProductInventory> productInventoryCaptor;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Test
    void testAddProduct_success() {
        when(typeRepository.findById(any(Long.class))).thenReturn(Optional.of(this.createValidType()));
        when(categoryService.findCategoryById(any(Long.class))).thenReturn(this.createValidCategory());
        when(productInventoryRepository.save(productInventoryCaptor.capture())).thenAnswer(invocation -> {
            ProductInventory productInventory = productInventoryCaptor.getValue();
            productInventory.setId(1L);
            return productInventory;
        });
        when(productRepository.save(productCaptor.capture())).thenAnswer(invocation -> {
            Product product = productCaptor.getValue();
            product.setId(1L);
            return product;
        });

        ProductRequestDto productRequestDto = this.createValidProductRequestDto();
        MultipartFile[] images = this.getValidImagesMultipartFiles();
        productService.addProduct(productRequestDto, images);
        verify(typeRepository, times(1)).findById(1L);
        verify(categoryService, times(1)).findCategoryById(1L);
        verify(productInventoryRepository, times(1)).save(any(ProductInventory.class));
        verify(productRepository, times(1)).save(any(Product.class));

        Product capturedProduct = productCaptor.getValue();
        ProductInventory capturedProductInventory = productInventoryCaptor.getValue();
        assertEquals(1L, capturedProduct.getId());
        assertEquals(productRequestDto.getName(), capturedProduct.getName());
        assertEquals(productRequestDto.getDescription(), capturedProduct.getDescription());
        assertEquals(productRequestDto.getPrice().setScale(2, RoundingMode.HALF_UP), capturedProduct.getPrice());
        assertEquals(1L, capturedProductInventory.getId());
        assertEquals(productRequestDto.getQuantity(), capturedProductInventory.getQuantity());
    }

    @Test
    void testAddProduct_productTypeNotFound() {
        when(typeRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        ProductRequestDto productRequestDto = this.createValidProductRequestDto();
        MultipartFile[] images = this.getValidImagesMultipartFiles();
        var exception = assertThrows(ProductException.class, () -> productService.addProduct(productRequestDto, images));
        assertEquals("Product type with 1 does not exists.", exception.getMessage());
    }

    @Test
    void testAddProduct_invalidQuantity() {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setQuantity(0);
        MultipartFile[] images = this.getValidImagesMultipartFiles();
        var exception = assertThrows(ProductException.class, () -> productService.addProduct(productRequestDto, images));
        assertEquals("Product quantity needs to be more than one.", exception.getMessage());
    }

    @Test
    void testAddProduct_invalidPrice() {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setQuantity(100);
        productRequestDto.setPrice(new BigDecimal("0.001"));
        MultipartFile[] images = this.getValidImagesMultipartFiles();
        var exception = assertThrows(ProductException.class, () -> productService.addProduct(productRequestDto, images));
        assertEquals("Invalid product price. Price needs to more than 0.00", exception.getMessage());
    }

    @Test
    void testAddProduct_invalidImagesNumber() {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setQuantity(100);
        productRequestDto.setPrice(new BigDecimal("100.00"));
        MultipartFile[] images = this.getValidImagesMultipartFilesOverMaximumSize();
        var exception = assertThrows(ProductException.class, () -> productService.addProduct(productRequestDto, images));
        assertEquals("Attach at least one or maximum five images.", exception.getMessage());
    }

    @Test
    void testAddProduct_invalidNoImagesAttached() {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setQuantity(100);
        productRequestDto.setPrice(new BigDecimal("100.00"));
        MultipartFile[] images = new MultipartFile[]{
                new MockMultipartFile("emptyImage", "emptyImage.png", "image/png", new byte[0])
        };
        var exception = assertThrows(ProductException.class, () -> productService.addProduct(productRequestDto, images));
        assertEquals("No images attached for product.", exception.getMessage());
    }

    @Test
    void testAddProduct_invalidImageContentType() {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setQuantity(100);
        productRequestDto.setPrice(new BigDecimal("100.00"));
        MultipartFile[] images = this.getInvalidImagesContentTypeMultipartFiles();
        var exception = assertThrows(ProductException.class, () -> productService.addProduct(productRequestDto, images));
        assertEquals("Invalid file content type.", exception.getMessage());
    }

    @Test
    void testFindProductById_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(this.createValidProduct()));
        Product expectedProduct = productService.findProductById(1L);
        assertEquals(1L, expectedProduct.getId());
        assertEquals("Test Product", expectedProduct.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testFindProductById_productNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        var exception = assertThrows(ProductException.class, () -> productService.findProductById(1L));
        assertEquals("Can not find product with given ID.", exception.getMessage());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProducts_success() {
        int pageNo = 0;
        int pageSize = 10;
        List<Product> products = Collections.singletonList(this.createValidProduct());
        Page<Product> page = new PageImpl<>(products);

        when(productRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(page);
        when(productImageRepository.findAllByProductId(any())).thenReturn(List.of(this.createValidProductImage()));
        when(minioService.getImageUrl("12345.png")).thenReturn("12345.png");

        Page<ProductResponseDto> productResponseDtoPage = productService.getProducts(pageNo, pageSize);

        assertEquals(products.size(), productResponseDtoPage.getContent().size());
        verify(productRepository, times(1)).findAllByOrderByCreatedAtDesc(any(Pageable.class));
        verify(productImageRepository, times(products.size())).findAllByProductId(1L);
        verify(minioService, times(1)).getImageUrl("12345.png");
    }

    @Test
    void testDeleteProductWithId_success() {
        Long productId = 1L;
        productService.deleteProductWithId(productId);
        verify(productRepository, times(1)).deleteById(productId);
        verify(minioService, times(1)).deleteProductImages(any());
    }

    @Test
    void testGetProductWithId_success() {
        Long productId = 1L;
        Product product = this.createValidProduct();
        ProductImage productImage = this.createValidProductImage();
        List<String> imagesUrls = Collections.singletonList("12345.png");

        when(productRepository.findById(productId)).thenReturn(Optional.ofNullable(product));
        when(productImageRepository.findAllByProductId(productId)).thenReturn(List.of(productImage));
        when(minioService.getImageUrl("12345.png")).thenReturn("12345.png");

        ProductResponseDto productResponseDto = productService.getProductWithId(productId);

        assertEquals(Objects.requireNonNull(product).getName(), productResponseDto.getName());
        assertEquals(imagesUrls, productResponseDto.getImagesUrls());
        verify(productRepository, times(1)).findById(productId);
        verify(productImageRepository, times(1)).findAllByProductId(productId);
        verify(minioService, times(1)).getImageUrl("12345.png");
    }

    private ProductRequestDto createValidProductRequestDto() {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setName("Test Product");
        productRequestDto.setDescription("Test description");
        productRequestDto.setQuantity(100);
        productRequestDto.setTypeId(1L);
        productRequestDto.setCategoryId(1L);
        productRequestDto.setPrice(new BigDecimal("100.00"));
        return productRequestDto;
    }

    private ProductImage createValidProductImage() {
        return ProductImage.builder()
                .id(1L)
                .imageNo("12345")
                .product(this.createValidProduct())
                .extension("png")
                .build();
    }

    private Product createValidProduct() {
        return Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test description")
                .type(this.createValidType())
                .category(this.createValidCategory())
                .price(new BigDecimal("100.00"))
                .build();
    }

    private Category createValidCategory() {
        return Category.builder()
                .id(1L)
                .name("Test category")
                .description("Test description")
                .build();
    }

    private Type createValidType() {
        Type type = new Type();
        type.setId(1L);
        type.setName(ProductType.MENS);
        type.setDescription("Test Type");
        return type;
    }

    private MultipartFile[] getValidImagesMultipartFiles() {
        return new MultipartFile[]{
                new MockMultipartFile("productImage1", "productImage1.jpg", "image/jpeg", "image1 content".getBytes()),
                new MockMultipartFile("productImage2", "productImage2.jpg", "image/jpg", "image2 content".getBytes()),
                new MockMultipartFile("productImage3", "productImage3.jpg", "image/png", "image3 content".getBytes())
        };
    }

    private MultipartFile[] getInvalidImagesContentTypeMultipartFiles() {
        return new MultipartFile[]{
                new MockMultipartFile("productImage1", "productImage1.jpg", "image/svg", "image1 content".getBytes()),
                new MockMultipartFile("productImage2", "productImage2.jpg", "image/jpg", "image2 content".getBytes()),
                new MockMultipartFile("productImage3", "productImage3.jpg", "image/png", "image3 content".getBytes())
        };
    }

    private MultipartFile[] getValidImagesMultipartFilesOverMaximumSize() {
        return new MultipartFile[]{
                new MockMultipartFile("productImage1", "productImage1.jpg", "image/jpeg", "image1 content".getBytes()),
                new MockMultipartFile("productImage2", "productImage2.jpg", "image/jpg", "image2 content".getBytes()),
                new MockMultipartFile("productImage3", "productImage3.jpg", "image/png", "image3 content".getBytes()),
                new MockMultipartFile("productImage4", "productImage4.jpg", "image/jpeg", "image4 content".getBytes()),
                new MockMultipartFile("productImage5", "productImage5.jpg", "image/jpg", "image5 content".getBytes()),
                new MockMultipartFile("productImage6", "productImage6.jpg", "image/png", "image6 content".getBytes())
        };
    }

}
