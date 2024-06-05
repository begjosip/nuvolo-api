package com.nuvolo.nuvoloapi.service;

import com.nuvolo.nuvoloapi.exceptions.InvalidCategoryException;
import com.nuvolo.nuvoloapi.model.dto.request.CategoryRequestDto;
import com.nuvolo.nuvoloapi.model.dto.response.CategoryResponseDto;
import com.nuvolo.nuvoloapi.model.entity.Category;
import com.nuvolo.nuvoloapi.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void testCreateCategory_category_already_exists() {
        CategoryRequestDto requestDto = this.createTestCategoryRequestDto();
        Category category = this.createTestCategoryEntity();
        when(categoryRepository.findByName(requestDto.getName())).thenReturn(Optional.ofNullable(category));
        assertThrows(InvalidCategoryException.class, () -> categoryService.createCategory(requestDto));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testCreateCategory_success() {
        CategoryRequestDto requestDto = this.createTestCategoryRequestDto();
        Category category = this.createTestCategoryEntity();
        when(categoryRepository.findByName(requestDto.getName())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> category);
        categoryService.createCategory(requestDto);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testFindCategoryById_categoryDoesNotExist() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(InvalidCategoryException.class, () -> categoryService.findCategoryById(1L));
    }

    @Test
    void testFindCategoryById_success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(this.createTestCategoryEntity()));
        Category foundCategory = categoryService.findCategoryById(1L);
        assertEquals(1L, foundCategory.getId());
        assertEquals("Test Category", foundCategory.getName());
        assertEquals("Test Description", foundCategory.getDescription());
    }

    @Test
    void testGetAllCategories_noCategoriesFound() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
        List<CategoryResponseDto> responseDtoList = categoryService.getAllCategories();
        assertEquals(0, responseDtoList.size());
    }

    @Test
    void testGetAllCategories_success() {
        List<Category> categories = List.of(this.createTestCategoryEntity());
        when(categoryRepository.findAll()).thenReturn(categories);
        List<CategoryResponseDto> responseDtoList = categoryService.getAllCategories();

        assertEquals(1L, responseDtoList.getFirst().getId());
        assertEquals("Test Category", responseDtoList.getFirst().getName());
        assertEquals("Test Description", responseDtoList.getFirst().getDescription());
    }


    private CategoryRequestDto createTestCategoryRequestDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Test Category");
        requestDto.setDescription("Test Description");
        return requestDto;
    }

    private Category createTestCategoryEntity() {
        return Category.builder()
                .id(1L)
                .name("Test Category")
                .description("Test Description")
                .build();
    }
}
