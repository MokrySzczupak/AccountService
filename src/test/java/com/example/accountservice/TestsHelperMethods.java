package com.example.accountservice;

import com.example.accountservice.controller.dto.PaymentDto;
import com.example.accountservice.model.Role;
import com.example.accountservice.model.SecurityEvent;
import com.example.accountservice.model.SecurityEventAction;
import com.example.accountservice.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestsHelperMethods {
    public static User createTestUser() {
        User testUser = new User();
        testUser.setEmail("testUser@acme.com");
        testUser.setName("testUserName");
        testUser.setLastname("testUserLastname");
        testUser.setPassword("T3stPa$$word");
        return testUser;
    }

    public static List<PaymentDto> createTestPaymentDtos() {
        List<PaymentDto> paymentDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PaymentDto paymentDto = new PaymentDto();
            paymentDto.setEmployee("testUser@acme.com");
            paymentDto.setSalary(i + 1000);
            paymentDto.setPeriod(String.format("%d-%d", i + 1, 2000 + i));
            paymentDtos.add(paymentDto);
        }
        return paymentDtos;
    }

    public static SecurityEvent createTestSecurityEvent() {
        SecurityEvent securityEvent = new SecurityEvent();
        securityEvent.setAction(SecurityEventAction.LOGIN_FAILED);
        securityEvent.setDate(LocalDate.now());
        return securityEvent;
    }
}
