package com.nuvolo.nuvoloapi.controller;

import com.nuvolo.nuvoloapi.model.dto.request.CategoryRequestDto;
import com.nuvolo.nuvoloapi.model.dto.request.DiscountRequestDto;
import com.nuvolo.nuvoloapi.model.dto.response.DiscountResponseDto;
import com.nuvolo.nuvoloapi.model.dto.response.UserResponseDto;
import com.nuvolo.nuvoloapi.service.CategoryService;
import com.nuvolo.nuvoloapi.service.DiscountService;
import com.nuvolo.nuvoloapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdministratorController {

    private final UserService userService;

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

}
