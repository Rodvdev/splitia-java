package com.splitia.security;

import com.splitia.controller.SupportController;
import com.splitia.service.SupportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(controllers = SupportController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupportService supportService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void unauthenticatedRequestReturns401WithJson() throws Exception {
        mockMvc.perform(get("/api/support/tickets").param("page", "0").param("size", "10"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().json("{\n  \"success\": false,\n  \"message\": \"Unauthorized\"\n}"));
    }
}