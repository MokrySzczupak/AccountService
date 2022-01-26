package account.controller;

import account.controller.dto.UserDto;
import account.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @PostMapping("/signup")
    public UserDto signup(@RequestBody @Valid User user) {
        return new UserDto(user);
    }
}
