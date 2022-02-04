package account.controller;

import account.model.SecurityEvent;
import account.service.SecurityEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/security")
public class SecurityController {
    @Autowired
    private SecurityEventService securityEventService;

    @GetMapping("/events")
    public List<SecurityEvent> getAllEvents() {
        return securityEventService.getAll();
    }
}
