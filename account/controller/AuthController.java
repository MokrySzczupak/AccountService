package account.controller;

import account.controller.dto.UserDto;
import account.model.User;
import account.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserDetailsServiceImp userDetailsService;
    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/signup")
    public UserDto signup(@RequestBody @Valid User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return new UserDto(userDetailsService.signup(user));
    }
}
