package com.nuvolo.nuvoloapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nuvolo.nuvoloapi.model.dto.request.UserRequestDto;
import com.nuvolo.nuvoloapi.util.HtmlUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/sql/Insert_test_users.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/cleanup.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@ActiveProfiles("test")
@DirtiesContext(
        classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthenticationControllerIntegrationTest {

    private static final String SIGN_IN_URI = "/v1/auth/sign-in";

    private static final String AUTH_VERIFY_URI = "/v1/auth/verify";

    private static final String VALID_PASSWORD = "nuvolo-test-password";

    private static final String VERIFICATION_UUID_TOKEN = "123e4567-e89b-12d3-a456-426614174000";

    private static final String VERIFICATION_UUID__VERIFIED_TOKEN = "e7e66e28-90a8-468b-ab33-a0bdf3002c49";


    @Autowired
    private MockMvc mvc;

    @Test
    void testUserSignIn_success() throws Exception {
        UserRequestDto signInUserRequest = UserRequestDto.builder()
                .email("verified@mail.com")
                .password(VALID_PASSWORD)
                .build();
        mvc.perform(MockMvcRequestBuilders.post(SIGN_IN_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(signInUserRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.firstName").value("nuvolo_first_name"))
                .andExpect(jsonPath("$.lastName").value("nuvolo_last_name"))
                .andExpect(jsonPath("$.email").value("verified@mail.com"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.isAdmin").value(Boolean.FALSE))
                .andExpect(status().isOk());
    }

    @Test
    void testUserSignIn_userNotVerified() throws Exception {
        UserRequestDto signInUserRequest = UserRequestDto.builder()
                .email("unverified@mail.com")
                .password(VALID_PASSWORD)
                .build();
        mvc.perform(MockMvcRequestBuilders.post(SIGN_IN_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(signInUserRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.title").value("User not verified or enabled!"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.detail").value("User is not verified. Check your email."))
                .andExpect(jsonPath("$.instance").value(SIGN_IN_URI))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUserSignIn_userNotFound() throws Exception {
        UserRequestDto signInUserRequest = UserRequestDto.builder()
                .email("notfound@mail.com")
                .password("password")
                .build();
        mvc.perform(MockMvcRequestBuilders.post(SIGN_IN_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(signInUserRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.title").value("Bad credentials!"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("Bad credentials"))
                .andExpect(jsonPath("$.instance").value(SIGN_IN_URI))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserVerificationWithToken_success() throws Exception {
        MvcResult response = mvc.perform(MockMvcRequestBuilders.post(AUTH_VERIFY_URI.concat("/").concat(VERIFICATION_UUID_TOKEN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(HtmlUtil.SUCCESSFUL_VERIFICATION_HTML, response.getResponse().getContentAsString());
    }

    @Test
    void testUserVerificationWithToken_userAlreadyVerified() throws Exception {
        MvcResult response = mvc.perform(MockMvcRequestBuilders
                        .post(AUTH_VERIFY_URI.concat("/").concat(VERIFICATION_UUID__VERIFIED_TOKEN)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        assertEquals(HtmlUtil.UNSUCCESSFUL_VERIFICATION_HTML, response.getResponse().getContentAsString());
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(object);
    }

}
