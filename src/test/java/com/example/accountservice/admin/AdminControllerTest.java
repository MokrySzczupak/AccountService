package com.example.accountservice.admin;

import com.example.accountservice.controller.dto.ChangeAccessDto;
import com.example.accountservice.controller.dto.ChangeRoleDto;
import com.example.accountservice.model.AccessOperation;
import com.example.accountservice.model.ChangeRoleOperation;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.example.accountservice.TestsHelperMethods.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

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
    public void shouldGetAllUsers() throws Exception {
        // given
        User userOne = createTestUser();
        userOne.addRole(Role.ROLE_ADMINISTRATOR);
        User userTwo = createTestUser();
        userTwo.setEmail("userTwo@acme.com");
        userDetailsService.signup(userOne, encoder);
        userDetailsService.signup(userTwo, encoder);
        UserDetails userOneDetails = userDetailsService.loadUserByUsername(userOne.getEmail());
        // when
        MvcResult result = mockMvc.perform(get("/api/admin/user")
                        .with(user(userOneDetails)))
                .andExpect(status().isOk())
                .andReturn();
        List<User> allUsers = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<User>>(){});
        // then
        assertThat(allUsers).isNotNull();
        assertThat(allUsers).hasSize(2);
    }

    @Test
    public void shouldDeleteUser() throws Exception {
        // given
        User userAdmin = createTestUser();
        userAdmin.addRole(Role.ROLE_ADMINISTRATOR);
        User userTwo = createTestUser();
        userTwo.setEmail("userTwo@acme.com");
        userDetailsService.signup(userAdmin, encoder);
        userDetailsService.signup(userTwo, encoder);
        UserDetails userAdminDetails = userDetailsService.loadUserByUsername(userAdmin.getEmail());
        // when
        mockMvc.perform(delete("/api/admin/user/" + userTwo.getEmail())
                .with(user(userAdminDetails)))
                .andExpect(status().isOk());
        // then
        User deletedUser = userRepository.findByEmailIgnoreCase(userTwo.getEmail());
        assertThat(deletedUser).isNull();
    }

    @Test
    public void shouldChangeUserRole() throws Exception {
        // given
        User userAdmin = createTestUser();
        userAdmin.addRole(Role.ROLE_ADMINISTRATOR);
        User userTwo = createTestUser();
        userTwo.setEmail("userTwo@acme.com");
        userDetailsService.signup(userAdmin, encoder);
        userDetailsService.signup(userTwo, encoder);
        UserDetails userAdminDetails = userDetailsService.loadUserByUsername(userAdmin.getEmail());
        ChangeRoleDto changeRoleDto = new ChangeRoleDto();
        changeRoleDto.setRole("ACCOUNTANT");
        changeRoleDto.setUser(userTwo.getEmail());
        changeRoleDto.setOperation(ChangeRoleOperation.GRANT);
        // when
        mockMvc.perform(put("/api/admin/user/role")
                .with(user(userAdminDetails))
                .content(objectMapper.writeValueAsString(changeRoleDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
        User updatedUser = userDetailsService.getUserByEmail(userTwo.getEmail());
        assertThat(updatedUser.getRoles()).contains(Role.ROLE_ACCOUNTANT);
    }

    @Test
    public void shouldChangeUserAccess() throws Exception {
        // given
        User userAdmin = createTestUser();
        userAdmin.addRole(Role.ROLE_ADMINISTRATOR);
        User userTwo = createTestUser();
        userTwo.setEmail("userTwo@acme.com");
        userDetailsService.signup(userAdmin, encoder);
        userDetailsService.signup(userTwo, encoder);
        UserDetails userAdminDetails = userDetailsService.loadUserByUsername(userAdmin.getEmail());
        ChangeAccessDto changeAccessDto = new ChangeAccessDto();
        changeAccessDto.setUser(userTwo.getEmail());
        changeAccessDto.setOperation(AccessOperation.LOCK);
        // when
        mockMvc.perform(put("/api/admin/user/access")
                .with(user(userAdminDetails))
                .content(objectMapper.writeValueAsString(changeAccessDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // then
        User updatedUser = userDetailsService.getUserByEmail(userTwo.getEmail());
        assertThat(updatedUser.isLockedAccount()).isTrue();
    }
}
