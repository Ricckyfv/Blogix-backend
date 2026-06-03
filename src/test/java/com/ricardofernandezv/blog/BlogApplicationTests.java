package com.ricardofernandezv.blog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ricardofernandezv.blog.domain.dtos.RegisterRequest;
import com.ricardofernandezv.blog.services.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.context.annotation.Import(BlogApplicationTests.TestConfig.class)
class BlogApplicationTests {

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @org.springframework.context.annotation.Bean
        public org.springframework.mail.javamail.JavaMailSender javaMailSender() {
            return org.mockito.Mockito.mock(org.springframework.mail.javamail.JavaMailSender.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetCategoriesAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testGetCategoriesAuthenticated() throws Exception {
        // 1. Register a user
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Test User")
                .email("test-diagnose@gmail.com")
                .password("password")
                .birthDate(LocalDate.of(2000, 1, 1))
                .description("Diagnostics description")
                .profileImage("avatar-preset")
                .build();

        UserDetails userDetails = authenticationService.register(registerRequest);
        String token = authenticationService.generateToken(userDetails);

        // 2. Perform authenticated GET request
        MvcResult result = mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andReturn();
        
        System.out.println("Authenticated Categories Status: " + result.getResponse().getStatus());
    }
}
