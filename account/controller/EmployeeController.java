package account.controller;

import account.controller.dto.UserDto;
import account.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/empl")
public class EmployeeController {

    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;

    @GetMapping("/payment")
    public UserDto getPayment(Authentication auth) {
        return new UserDto(userDetailsServiceImp.loadLoggedUser(auth));
    }
}
