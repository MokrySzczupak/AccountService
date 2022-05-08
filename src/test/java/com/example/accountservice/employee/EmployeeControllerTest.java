package com.example.accountservice.employee;

import com.example.accountservice.controller.dto.PaymentDto;
import com.example.accountservice.model.Role;
import com.example.accountservice.model.User;
import com.example.accountservice.repository.PaymentRepository;
import com.example.accountservice.repository.UserRepository;
import com.example.accountservice.service.PaymentService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.example.accountservice.TestsHelperMethods.createTestPaymentDtos;
import static com.example.accountservice.TestsHelperMethods.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

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

    @BeforeEach
    public void cleanTestData() {
        paymentRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    public void shouldGetPayments() throws Exception {
        // given
        User newUser = createTestUser();
        newUser.addRole(Role.ROLE_ACCOUNTANT);
        userDetailsService.signup(newUser, encoder);
        UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getEmail());
        List<PaymentDto> paymentDtos = createTestPaymentDtos();
        paymentService.savePayments(paymentDtos);
        // when
        MvcResult result = mockMvc.perform(get("/api/empl/payment")
                .with(user(userDetails)))
                .andExpect(status().isOk())
                .andReturn();
        // then
        List<Object> payments = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {});
        assertThat(payments).isNotNull();
        assertThat(payments).hasSize(paymentDtos.size());
    }
}
