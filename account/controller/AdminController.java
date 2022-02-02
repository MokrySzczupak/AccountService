package account.controller;

import account.controller.dto.ChangeRoleDto;
import account.controller.dto.UserDto;
import account.model.User;
import account.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    @Autowired
    private UserDetailsServiceImp userDetailsService;

    @GetMapping("/user")
    public List<UserDto> getAllUsers() {
        return userDetailsService.getAllUsers()
                .stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/user/{email}")
    public Map<String, String> deleteUser(@PathVariable String email) {
        userDetailsService.deleteUser(email);
        return new ConcurrentHashMap<>(Map.of("user", email,
                "status", "Deleted successfully!"));
    }

    @PutMapping("/user/role")
    public UserDto changeRole(@RequestBody ChangeRoleDto changeRoleDto) {
        User user = userDetailsService.changeUserRole(changeRoleDto);
        return new UserDto(user);
    }
}
