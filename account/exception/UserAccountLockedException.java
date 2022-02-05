package account.exception;

import org.springframework.security.authentication.LockedException;

public class UserAccountLockedException extends LockedException {
    public UserAccountLockedException() {
        super("User account is locked");
    }
}
