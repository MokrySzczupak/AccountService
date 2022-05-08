package com.example.accountservice.auth;

import com.example.accountservice.model.User;
import com.example.accountservice.repository.PaymentRepository;
import com.example.accountservice.repository.UserRepository;
import com.example.accountservice.service.UserDetailsServiceImp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.accountservice.TestsHelperMethods.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

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
    public PaymentRepository paymentRepository;

    @BeforeEach
    public void cleanTestData() {
        paymentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldSignupSuccessfully() throws Exception {
        // given
        User newUser = createTestUser();
        // when
        mockMvc.perform(post("/api/auth/signup")
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
        User addedUser = userRepository.findByEmailIgnoreCase(newUser.getEmail());
        assertThat(addedUser).isNotNull();
        assertThat(addedUser.getEmail()).isEqualToIgnoringCase(newUser.getEmail());
    }

    @Test
    public void shouldChangeUserPassword() throws Exception {
        // given
        User newUser = createTestUser();
        userDetailsService.signup(newUser, encoder);
        String passwordDtoJson = "{\"new_password\": \"changedPassword\"}";
        // when
        mockMvc.perform(post("/api/auth/changepass")
                        .with(user(newUser.getEmail()))
                        .content(passwordDtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
        User userWithChangedPassword = userRepository.findByEmailIgnoreCase(newUser.getEmail());
        assertThat(userWithChangedPassword.getPassword()).isNotEqualTo(newUser.getPassword());
    }
}
