package account.controller;

import account.controller.dto.PasswordDto;
import account.controller.dto.UserDto;
import account.model.User;
import account.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/signup")
    public UserDto signup(@RequestBody @Valid User user) {
        return new UserDto(userDetailsService.signup(user, encoder));
    }

    @PostMapping("/changepass")
    public Map<String, String> changePassword(Authentication auth,
                                              @RequestBody PasswordDto passwordDto) {
        User user = userDetailsService.loadLoggedUser(auth);
        userDetailsService.changePassword(user, encoder, passwordDto.getPassword());
        return new ConcurrentHashMap<>(Map.of("email", user.getEmail().toLowerCase(),
                "status", "The password has been updated successfully"));
    }
}
