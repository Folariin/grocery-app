package com.mygroceries.backend;

import com.mygroceries.backend.config.SecurityConfig;
import com.mygroceries.backend.controller.HealthController;
import com.mygroceries.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
@Import({SecurityConfig.class, HealthControllerTest.JwtTestConfig.class})
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class, inheritListeners = false)
class HealthControllerTest {
    @Autowired MockMvc mvc;
    @TestConfiguration
    static class JwtTestConfig {
        @Bean
        JwtService jwtService() {
            return new JwtService("local-test-signing-key-with-at-least-32-characters", 60000, "local");
        }
    }

    @Test
    void healthIsAnonymousAndMinimal() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().json("{\"status\":\"UP\"}", true));
    }

    @Test
    void otherRoutesStillRequireAuthentication() throws Exception {
        mvc.perform(get("/api/households")).andExpect(status().isForbidden());
    }
}
