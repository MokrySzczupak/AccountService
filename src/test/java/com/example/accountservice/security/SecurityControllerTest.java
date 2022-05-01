package com.example.accountservice.security;

import com.example.accountservice.model.Role;
import com.example.accountservice.model.SecurityEvent;
import com.example.accountservice.model.User;
import com.example.accountservice.repository.PaymentRepository;
import com.example.accountservice.repository.SecurityEventRepository;
import com.example.accountservice.repository.UserRepository;
import com.example.accountservice.service.PaymentService;
import com.example.accountservice.service.SecurityEventService;
import com.example.accountservice.service.UserDetailsServiceImp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.example.accountservice.TestsHelperMethods.createTestSecurityEvent;
import static com.example.accountservice.TestsHelperMethods.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityControllerTest {

    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public UserDetailsServiceImp userDetailsService;
    @Autowired
    public PasswordEncoder encoder;
    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    public PaymentService paymentService;
    @Autowired
    public PaymentRepository paymentRepository;
    @Autowired
    public SecurityEventService securityEventService;
    @Autowired
    public SecurityEventRepository securityEventRepository;

    @BeforeEach
    public void cleanTestData() {
        paymentRepository.deleteAll();
        userRepository.deleteAll();
        securityEventRepository.deleteAll();
    }

    @Test
    public void shouldGetAllEvents() throws Exception {
        // given
        User auditorUser = createTestUser();
        auditorUser.addRole(Role.ROLE_AUDITOR);
        userDetailsService.signup(auditorUser, encoder);
        UserDetails auditorUserDetails = userDetailsService.loadUserByUsername(auditorUser.getEmail());
        securityEventService.addSecurityEvent(createTestSecurityEvent());
        // when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/security/events")
                        .with(SecurityMockMvcRequestPostProcessors.user(auditorUserDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // then
        List<SecurityEvent> securityEvents = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>(){});
        assertThat(securityEvents).isNotNull();
        assertThat(securityEvents).hasSize(1);
    }
}
