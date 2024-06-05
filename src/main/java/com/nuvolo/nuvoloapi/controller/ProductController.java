package com.nuvolo.nuvoloapi.controller;

import com.nuvolo.nuvoloapi.model.dto.response.ProductResponseDto;
import com.nuvolo.nuvoloapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Object> getProducts(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info(" > > > GET /api/v1/product");
        Page<ProductResponseDto> products = productService.getProducts(page, size);
        log.info(" < < < GET /api/v1/product");
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductWithId(@PathVariable Long id) {
        log.info(" > > > GET /api/v1/product/{}", id);
        ProductResponseDto product = productService.getProductWithId(id);
        log.info(" < < < GET /api/v1/product/{}", id);
        return ResponseEntity.ok(product);
    }


}
