package com.example.accountservice.accountant;

import com.example.accountservice.controller.dto.PaymentDto;
import com.example.accountservice.model.Payment;
import com.example.accountservice.model.Role;
import com.example.accountservice.model.User;
import com.example.accountservice.repository.PaymentRepository;
import com.example.accountservice.repository.UserRepository;
import com.example.accountservice.service.PaymentService;
import com.example.accountservice.service.UserDetailsServiceImp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.example.accountservice.TestsHelperMethods.createTestPaymentDtos;
import static com.example.accountservice.TestsHelperMethods.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountantControllerTest {

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
    public void shouldPostPayments() throws Exception {
        // given
        User newUser = createTestUser();
        newUser.addRole(Role.ROLE_ACCOUNTANT);
        userDetailsService.signup(newUser, encoder);
//        userRepository.save(newUser);
        UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getEmail());
        List<PaymentDto> paymentDtos = createTestPaymentDtos();
        // when
        mockMvc.perform(post("/api/acct/payments")
                        .with(user(userDetails))
                .content(objectMapper.writeValueAsString(paymentDtos))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
        List<Payment> payments = paymentService.getPaymentsForEmployee("testuser@acme.com");
        assertThat(payments).isNotNull();
        assertThat(payments).hasSize(10);
    }

    @Test
    public void shouldUpdatePayment() throws Exception {
        // given
        User newUser = createTestUser();
        newUser.addRole(Role.ROLE_ACCOUNTANT);
        userDetailsService.signup(newUser, encoder);
        UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getEmail());
        List<PaymentDto> paymentDtos = createTestPaymentDtos();
        paymentService.savePayments(paymentDtos);
        // when
        PaymentDto paymentToEdit = paymentDtos.get(0);
        paymentToEdit.setSalary(999999L);
        mockMvc.perform(put("/api/acct/payments")
                .with(user(userDetails))
                        .content(objectMapper.writeValueAsString(paymentToEdit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
        Payment editedPayment = paymentService.getPaymentForPeriodAndEmployee(paymentToEdit.getPeriod(),
                paymentToEdit.getEmployee());
        assertThat(editedPayment).isNotNull();
        assertThat(editedPayment.getSalary()).isEqualTo("9999 dollar(s) 99 cent(s)");
    }
}
