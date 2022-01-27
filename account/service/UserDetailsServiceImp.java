package account.service;

import account.exception.UserExistException;
import account.model.User;
import account.model.UserDetailsImp;
import account.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
    public User signup(User user) {
        if (userExist(user.getEmail())) {
            throw new UserExistException();
        }
        return userRepository.save(user);
    }

    private boolean userExist(String email) {
        return userRepository.findByEmailIgnoreCase(email) != null;
    }
}
