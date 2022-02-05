package account.exception;

import account.model.SecurityEventAction;
import account.security.SecurityEventLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessDeniedHandlerImp implements AccessDeniedHandler {

    @Autowired
    private SecurityEventLogger securityEventLogger;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException, ServletException {
        securityEventLogger.logEvent(SecurityEventAction.ACCESS_DENIED, request.getPathTranslated());
        response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied!");
    }
}
