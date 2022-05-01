package com.example.accountservice.auth;

import com.example.accountservice.model.User;
import com.example.accountservice.repository.PaymentRepository;
import com.example.accountservice.repository.UserRepository;
import com.example.accountservice.service.UserDetailsServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.accountservice.TestsHelperMethods.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthServiceTest {

    @Autowired
    public UserRepository userRepository;
    @Autowired
    public UserDetailsServiceImp userDetailsService;
    @Autowired
    public PasswordEncoder encoder;
    @Autowired
    public PaymentRepository paymentRepository;

    @BeforeEach
    public void cleanTestData() {
        paymentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldSignupSuccessfully() {
        // given
        User newUser = createTestUser();
        // when
        userDetailsService.signup(newUser, encoder);
        // then
        User addedUser = userRepository.findByEmailIgnoreCase(newUser.getEmail());
        assertThat(addedUser).isNotNull();
        assertThat(addedUser.getEmail()).isEqualToIgnoringCase(newUser.getEmail());
    }

    @Test
    public void shouldChangeUserPassword() {
        // given
        User newUser = createTestUser();
        userDetailsService.signup(newUser, encoder);
        String newPassword = "changedPassword";
        // when
        userDetailsService.changePassword(newUser, encoder, newPassword);
        // then
        User userWithChangedPassword = userRepository.findByEmailIgnoreCase(newUser.getEmail());
        assertThat(userWithChangedPassword.getPassword()).isNotEqualTo(newUser.getPassword());
    }
}
