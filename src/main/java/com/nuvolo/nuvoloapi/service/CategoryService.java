package com.nuvolo.nuvoloapi.service;

import com.nuvolo.nuvoloapi.exceptions.InvalidCategoryException;
import com.nuvolo.nuvoloapi.model.dto.request.CategoryRequestDto;
import com.nuvolo.nuvoloapi.model.dto.response.CategoryResponseDto;
import com.nuvolo.nuvoloapi.model.entity.Category;
import com.nuvolo.nuvoloapi.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void createCategory(CategoryRequestDto categoryRequest) {
        if (categoryRepository.findByName(categoryRequest.getName()).isPresent()) {
            throw new InvalidCategoryException("Category with given name already exists.");
        }
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .build();
        log.debug("Saving category entity to database.");
        Category savedCategory = categoryRepository.save(category);
        log.debug("Category with ID:{} saved to database.", savedCategory.getId());
    }

    public Category findCategoryById(Long categoryId) {
        log.debug("Finding product category with ID: {}", categoryId);
        return categoryRepository.findById(categoryId).orElseThrow(() -> new InvalidCategoryException(
                String.format("Category with ID: %s does not exist", categoryId)));
    }

    public List<CategoryResponseDto> getAllCategories() {
        log.debug("Fetching all categories from database.");
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            log.debug("No categories saved in database. Returning empty list.");
            return Collections.emptyList();
        }
        log.debug("Successfully retrieved categories from database.");
        return categories.stream().map(CategoryResponseDto::mapCategoryEntity).toList();
    }
}
