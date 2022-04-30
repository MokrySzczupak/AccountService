package com.example.accountservice.controller;

import com.example.accountservice.controller.dto.ChangeAccessDto;
import com.example.accountservice.controller.dto.ChangeRoleDto;
import com.example.accountservice.controller.dto.UserDto;
import com.example.accountservice.model.ChangeRoleOperation;
import com.example.accountservice.model.SecurityEventAction;
import com.example.accountservice.model.User;
import com.example.accountservice.security.SecurityEventLogger;
import com.example.accountservice.service.UserDetailsServiceImp;
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
