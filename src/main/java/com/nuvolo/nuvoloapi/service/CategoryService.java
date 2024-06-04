package com.nuvolo.nuvoloapi.service;

import com.nuvolo.nuvoloapi.exceptions.InvalidCategoryException;
import com.nuvolo.nuvoloapi.model.dto.request.CategoryRequestDto;
import com.nuvolo.nuvoloapi.model.entity.Category;
import com.nuvolo.nuvoloapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

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
}
