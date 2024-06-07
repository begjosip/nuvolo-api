package com.nuvolo.nuvoloapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nuvolo.nuvoloapi.model.dto.request.CategoryRequestDto;
import com.nuvolo.nuvoloapi.model.dto.request.DiscountRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/sql/Insert_test_discount.sql", "/sql/Insert_test_users.sql", "/sql/Insert_test_category.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/Insert_test_discount.sql", "/sql/cleanup.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@ActiveProfiles("test")
@DirtiesContext(
        classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdministratorControllerIntegrationTest {

    private static final String DELETE_PRODUCT_ID_URI = "/v1/admin/product/10000";

    private static final String USERS_URI = "/v1/admin/users";

    private static final String DISCOUNT_URI = "/v1/admin/discount";

    private static final String CATEGORY_URI = "/v1/admin/category";

    private static final LocalDateTime FIXED_START_DATETIME = LocalDateTime.of(2030, 7, 1, 12, 0);

    private static final LocalDateTime FIXED_END_DATETIME = LocalDateTime.of(2030, 7, 2, 12, 0);

    @Autowired
    private MockMvc mvc;

    @WithMockUser(roles = "ADMIN")
    @Test
    void testGetAllUsers_success() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(USERS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].firstName").value("nuvolo_first_name"))
                .andExpect(jsonPath("$[0].lastName").value("nuvolo_last_name"))
                .andExpect(jsonPath("$[0].email").value("nuvolo@mail.com"))
                .andExpect(jsonPath("$[0].isAdmin").value("false"))
                .andExpect(jsonPath("$[0].isEnabled").value("false"));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void testGetAllDiscounts_success() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(DISCOUNT_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Test discount"))
                .andExpect(jsonPath("$[0].description").value("Discount description"))
                .andExpect(jsonPath("$[0].discountPercentage").value("0.1"))
                .andExpect(jsonPath("$[0].startDate").value("2030-01-01T12:00:00"))
                .andExpect(jsonPath("$[0].endDate").value("2030-02-01T12:00:00"))
                .andExpect(jsonPath("$[0].active").value("true"));
    }

    @Test
    void testCreateDiscount_forbiddenUser() throws Exception {
        DiscountRequestDto discountRequestDto = createValidDiscountRequestDto();
        mvc.perform(MockMvcRequestBuilders.post(DISCOUNT_URI)
                        .content(asJsonString(discountRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.status").value("401"))
                .andExpect(jsonPath("$.detail").value("Full authentication is required to access this resource"))
                .andExpect(jsonPath("$.instance").value(DISCOUNT_URI));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void testCreateCategory_success() throws Exception {
        CategoryRequestDto categoryRequestDto = this.createValidCategoryRequestDto();
        mvc.perform(MockMvcRequestBuilders.post(CATEGORY_URI)
                        .content(asJsonString(categoryRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void testCreateDiscount_success() throws Exception {
        DiscountRequestDto discountRequestDto = this.createValidDiscountRequestDto();
        mvc.perform(MockMvcRequestBuilders.post(DISCOUNT_URI)
                        .content(asJsonString(discountRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    void testDeleteProductWithId_noProductFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete(DELETE_PRODUCT_ID_URI))
                .andExpect(status().isNoContent());
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(object);
    }

    private DiscountRequestDto createValidDiscountRequestDto() {
        return DiscountRequestDto.builder()
                .name("Test discount")
                .description("Discount description")
                .startDate(FIXED_START_DATETIME)
                .endDate(FIXED_END_DATETIME)
                .discountPercentage(new BigDecimal("0.100"))
                .build();
    }

    private CategoryRequestDto createValidCategoryRequestDto() {
        return CategoryRequestDto.builder()
                .name("Test category")
                .description("Category description")
                .build();
    }

}
