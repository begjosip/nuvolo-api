package com.nuvolo.nuvoloapi.controller;

import com.nuvolo.nuvoloapi.model.dto.request.CategoryRequestDto;
import com.nuvolo.nuvoloapi.model.dto.request.DiscountRequestDto;
import com.nuvolo.nuvoloapi.model.dto.request.ProductRequestDto;
import com.nuvolo.nuvoloapi.model.dto.response.DiscountResponseDto;
import com.nuvolo.nuvoloapi.model.dto.response.UserResponseDto;
import com.nuvolo.nuvoloapi.service.CategoryService;
import com.nuvolo.nuvoloapi.service.DiscountService;
import com.nuvolo.nuvoloapi.service.ProductService;
import com.nuvolo.nuvoloapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdministratorController {

    private final UserService userService;

    private final ProductService productService;

    private final CategoryService categoryService;

    private final DiscountService discountService;

    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers() {
        log.info(" > > > GET /api/v1/admin/users");
        List<UserResponseDto> users = userService.getAllUsers();
        log.info(" < < < GET /api/v1/admin/users");
        return ResponseEntity.ok(users);
    }

    @GetMapping("/discount")
    public ResponseEntity<Object> getAllDiscounts() {
        log.info(" > > > GET /api/v1/admin/discount");
        List<DiscountResponseDto> discounts = discountService.getAllDiscounts();
        log.info(" < < < GET /api/v1/admin/discount");
        return ResponseEntity.ok(discounts);
    }

    @PostMapping("/discount")
    public ResponseEntity<Object> createDiscount(@Validated @RequestBody DiscountRequestDto discountRequest) {
        log.info(" > > > POST /api/v1/admin/discount");
        discountService.createDiscount(discountRequest);
        log.info(" < < < POST /api/v1/admin/discount");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/category")
    public ResponseEntity<Object> createCategory(@Validated @RequestBody CategoryRequestDto categoryRequest) {
        log.info(" > > > POST /api/v1/admin/category");
        categoryService.createCategory(categoryRequest);
        log.info(" < < < POST /api/v1/admin/category");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> addProduct(@Validated @ModelAttribute ProductRequestDto productRequest,
                                             @RequestParam MultipartFile[] images) {
        log.info(" > > > POST /api/v1/admin/product");
        productService.addProduct(productRequest, images);
        log.info(" < < < POST /api/v1/admin/product");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping(value = "/product/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long id) {
        log.info(" > > > POST /api/v1/admin/product/{}", id);
        productService.deleteProductWithId(id);
        log.info(" < < < POST /api/v1/admin/product/{}", id);
        return ResponseEntity.noContent().build();
    }

}
