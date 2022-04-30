package com.example.accountservice.service;

import com.example.accountservice.model.SecurityEvent;
import com.example.accountservice.repository.SecurityEventRepository;
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
