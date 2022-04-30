package com.example.accountservice.controller;

import com.example.accountservice.controller.dto.PasswordDto;
import com.example.accountservice.controller.dto.UserDto;
import com.example.accountservice.model.SecurityEventAction;
import com.example.accountservice.model.User;
import com.example.accountservice.security.SecurityEventLogger;
import com.example.accountservice.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserDetailsServiceImp userDetailsService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private SecurityEventLogger eventLogger;

    @PostMapping("/signup")
    public UserDto signup(@RequestBody @Valid User user) {
        User registeredUser = userDetailsService.signup(user, encoder);
        eventLogger.logEvent(SecurityEventAction.CREATE_USER, user.getEmail());
        return new UserDto(registeredUser);
    }

    @PostMapping("/changepass")
    public Map<String, String> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody PasswordDto passwordDto) {
        User user = userDetailsService.getUserByEmail(userDetails.getUsername());
        userDetailsService.changePassword(user, encoder, passwordDto.getPassword());
        eventLogger.logEvent(SecurityEventAction.CHANGE_PASSWORD, user.getEmail());
        return new ConcurrentHashMap<>(Map.of("email", user.getEmail().toLowerCase(),
                "status", "The password has been updated successfully"));
    }
}
