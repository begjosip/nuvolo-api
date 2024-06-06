package com.nuvolo.nuvoloapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/sql/Insert_test_category.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

    private static final String CATEGORIES_URI = "/v1/category";

    @Autowired
    private MockMvc mvc;

    @Test
    void testGetAllCategories_success() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get(CATEGORIES_URI)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Category name"))
                .andExpect(jsonPath("$[0].description").value("Category description"));
    }
}
