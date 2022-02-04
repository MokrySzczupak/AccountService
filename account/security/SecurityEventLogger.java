package account.security;

import account.model.SecurityEvent;
import account.model.SecurityEventAction;
import account.service.SecurityEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@Component
public class SecurityEventLogger {

    @Autowired
    private SecurityEventService securityEventService;

    @Autowired
    private HttpServletRequest request;

    public void logEvent(SecurityEventAction action, String object) {
        String path = request.getServletPath();
        Authentication subjectAuth = SecurityContextHolder.getContext().getAuthentication();
        createEvent(action, getProperSubject(subjectAuth), getProperObject(object, path));
    }

    private String getProperSubject(Authentication subjectAuth) {
        return subjectAuth == null
                || subjectAuth.getName().equals("anonymousUser")
                ? "Anonymous"
                : subjectAuth.getName().toLowerCase();
    }

    private String getProperObject(String object, String path) {
        return object == null ? path : object;
    }

    public void logLoginFailedEvent(String subject) {
        createEvent(SecurityEventAction.LOGIN_FAILED, subject, request.getServletPath());
    }

    public void logBruteForceEvent(String subject) {
        createEvent(SecurityEventAction.BRUTE_FORCE, subject, request.getServletPath());
    }

    public void logLockUserEvent(String subject) {
        String subjectFormatted = "Lock user " + subject.toLowerCase();
        createEvent(SecurityEventAction.LOCK_USER, subject, subjectFormatted);
    }

    private void createEvent(SecurityEventAction action, String subject, String object) {
        String path = request.getServletPath();
        SecurityEvent event = new SecurityEvent();
        event.setAction(action);
        event.setDate(LocalDate.now());
        event.setObject(object);
        event.setSubject(subject);
        event.setPath(path);
        securityEventService.addSecurityEvent(event);
    }
}
