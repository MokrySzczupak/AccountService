package account.controller;

import account.controller.dto.PasswordDto;
import account.controller.dto.UserDto;
import account.model.SecurityEventAction;
import account.model.User;
import account.security.SecurityEventLogger;
import account.service.UserDetailsServiceImp;
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
