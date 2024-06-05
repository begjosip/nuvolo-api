package com.nuvolo.nuvoloapi.model.dto.response;

import com.nuvolo.nuvoloapi.model.entity.Category;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CategoryResponseDto {

    private Long id;

    private String name;

    private String description;

    public static CategoryResponseDto mapCategoryEntity(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
