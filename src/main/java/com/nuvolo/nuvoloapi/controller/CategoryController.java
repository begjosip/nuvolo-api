package com.nuvolo.nuvoloapi.controller;

import com.nuvolo.nuvoloapi.model.dto.response.CategoryResponseDto;
import com.nuvolo.nuvoloapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Object> getCategories() {
        log.info(" > > > GET /api/v1/category");
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        log.info(" < < < GET /api/v1/category");
        return ResponseEntity.ok(categories);
    }

}
