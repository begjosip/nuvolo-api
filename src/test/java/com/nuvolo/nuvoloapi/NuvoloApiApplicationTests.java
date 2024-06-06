package com.nuvolo.nuvoloapi;

import com.nuvolo.nuvoloapi.controller.CategoryController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class NuvoloApiApplicationTests {

    @Autowired
    private CategoryController categoryController;

    @Test
    void contextLoads() {
        assertThat(categoryController).isNotNull();
    }

}
