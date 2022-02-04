package account.security;

import account.exception.UserAccountLockedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointImp implements AuthenticationEntryPoint {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String errorMsg = "Unauthorized";
        if (authException instanceof LockedException) {
            errorMsg = "User account is locked";
        }
        response.sendError(HttpStatus.UNAUTHORIZED.value(), errorMsg);
    }

    public void throwLockedException() {
        try {
            commence(request, response, new UserAccountLockedException());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
