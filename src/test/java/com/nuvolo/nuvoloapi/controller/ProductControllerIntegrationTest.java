package com.nuvolo.nuvoloapi.controller;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(
        classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerIntegrationTest {

    private static final String PRODUCT_URI = "/v1/product";

    private static final String PRODUCT_WITH_ID_NOT_FOUND_URI = "/v1/product/1000";


    @Autowired
    private MockMvc mvc;

    @Test
    void getProductWithID_productWithIdNotFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(PRODUCT_WITH_ID_NOT_FOUND_URI))
                .andDo(print())
                .andExpect(jsonPath("$.title").value("Product error occurred!"))
                .andExpect(jsonPath("$.detail").value("Can not find product with given ID."))
                .andExpect(jsonPath("$.instance").value(PRODUCT_WITH_ID_NOT_FOUND_URI))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllProducts_noProductsFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(PRODUCT_URI))
                .andDo(print())
                .andExpect(jsonPath("$.content").value(IsEmptyCollection.empty()))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.pageable.sort.empty").value(true))
                .andExpect(status().isOk());
    }
}
