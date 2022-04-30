package com.example.accountservice.service;

import com.example.accountservice.model.User;
import com.example.accountservice.security.SecurityEventLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    private final int MAX_ATTEMPTS = 5;
    private final Map<String, Integer> usersAttemptsCounters = new ConcurrentHashMap<>();

    @Autowired
    private SecurityEventLogger eventLogger;

    @Autowired
    private UserDetailsServiceImp userDetailsService;

    public void loginSucceeded(final String name) {
        usersAttemptsCounters.remove(getValidKey(name));
    }

    private String getValidKey(final String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT);
    }

    @Transactional
    public void loginFailed(Authentication auth) {
        int counter = usersAttemptsCounters.getOrDefault(getValidKey(auth.getName()), 0);
        counter++;
        eventLogger.logLoginFailedEvent(auth.getName());
        if (counter == MAX_ATTEMPTS) {
            User user = userDetailsService.getUserByEmail(auth.getName());
            if (!user.isAdmin()) {
                eventLogger.logBruteForceEvent(auth.getName());
                eventLogger.logLockUserEvent(auth.getName());
                user.setLockedAccount(true);
            }
        }
        usersAttemptsCounters.put(getValidKey(auth.getName()), counter);
    }
}
