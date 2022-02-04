package account.service;

import account.model.SecurityEvent;
import account.repository.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityEventService {
    @Autowired
    private SecurityEventRepository eventRepository;

    public void addSecurityEvent(SecurityEvent event) {
        eventRepository.save(event);
    }

    public List<SecurityEvent> getAll() {
        return eventRepository.findAllByOrderByIdAsc();
    }
}
