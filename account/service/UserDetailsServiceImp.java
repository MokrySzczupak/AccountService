package account.service;

import account.exception.BreachedPasswordException;
import account.exception.IdenticalPasswordException;
import account.exception.PasswordLengthException;
import account.exception.UserExistException;
import account.model.User;
import account.model.UserDetailsImp;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepository userRepository;

    private final short MINIMUM_PASSWORD_LENGTH = 12;

    @Autowired
    public UserDetailsServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final List<String> breachedPasswords = new ArrayList<>(List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"));

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(username);
        if (user == null) {
            throw new UsernameNotFoundException("User '" + username + "' does not exist");
        }
        return new UserDetailsImp(user);
    }

    public User loadLoggedUser(Authentication auth) {
        User user = userRepository.findByEmailIgnoreCase(auth.getName());
        if (user == null) {
            throw new UsernameNotFoundException("User '" + auth.getName() + "' does not exist");
        }
        return user;
    }

    @Transactional(rollbackOn = UserExistException.class)
    public User signup(User user, PasswordEncoder encoder) {
        if (userExist(user.getEmail())) {
            throw new UserExistException();
        }
        validatePassword(user.getPassword());
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean userExist(String email) {
        return userRepository.findByEmailIgnoreCase(email) != null;
    }

    @Transactional(rollbackOn = Exception.class)
    public void changePassword(User user, PasswordEncoder encoder, String newPass) {
        if (encoder.matches(newPass, user.getPassword())) {
            throw new IdenticalPasswordException();
        }
        validatePassword(newPass);
        user.setPassword(encoder.encode(newPass));
    }

    public void validatePassword(String password) {
        if (breachedPasswords.contains(password)) {
            throw new BreachedPasswordException();
        }
        if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            throw new PasswordLengthException();
        }
    }
}
