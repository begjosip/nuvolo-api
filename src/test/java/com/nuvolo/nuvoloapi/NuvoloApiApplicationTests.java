package com.nuvolo.nuvoloapi;

import com.nuvolo.nuvoloapi.controller.AdministratorController;
import com.nuvolo.nuvoloapi.controller.AuthenticationController;
import com.nuvolo.nuvoloapi.controller.CategoryController;
import com.nuvolo.nuvoloapi.controller.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class NuvoloApiApplicationTests {

    @Autowired
    private AuthenticationController authenticationController;

    @Autowired
    private AdministratorController administratorController;

    @Autowired
    private ProductController productController;

    @Autowired
    private CategoryController categoryController;


    @Test
    void contextLoads() {
        assertThat(authenticationController).isNotNull();
        assertThat(administratorController).isNotNull();
        assertThat(productController).isNotNull();
        assertThat(categoryController).isNotNull();
    }

}
