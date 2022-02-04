package account.controller;

import account.controller.dto.ChangeAccessDto;
import account.controller.dto.ChangeRoleDto;
import account.controller.dto.UserDto;
import account.model.ChangeRoleOperation;
import account.model.SecurityEventAction;
import account.model.User;
import account.security.SecurityEventLogger;
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
    @Autowired
    private SecurityEventLogger eventLogger;

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
        eventLogger.logEvent(SecurityEventAction.DELETE_USER, email);
        return new ConcurrentHashMap<>(Map.of("user", email,
                "status", "Deleted successfully!"));
    }

    @PutMapping("/user/role")
    public UserDto changeRole(@RequestBody ChangeRoleDto changeRoleDto) {
        User user = userDetailsService.changeUserRole(changeRoleDto);
        boolean grant = changeRoleDto.getOperation().equals(ChangeRoleOperation.GRANT);
        eventLogger.logEvent(SecurityEventAction.valueOf(changeRoleDto.getOperation() + "_ROLE"),
                changeRoleDto.getOperation().getFormattedName() + " role " + changeRoleDto.getRole() + (grant ? " to " : " from ") + changeRoleDto.getUser().toLowerCase());
        return new UserDto(user);
    }

    @PutMapping("/user/access")
    public Map<String, String> changeUserAccess(@RequestBody ChangeAccessDto accessDto) {
        userDetailsService.changeUserAccess(accessDto);
        String accessOperation = accessDto.getOperation().getFormattedName();
        eventLogger.logEvent(SecurityEventAction.valueOf(accessDto.getOperation() + "_USER"),
                accessOperation + " user " + accessDto.getUser().toLowerCase());
        return new ConcurrentHashMap<>(Map.of("status",
                "User " + accessDto.getUser().toLowerCase() + " "
                        + accessOperation.toLowerCase() + "ed!"));
    }
}
