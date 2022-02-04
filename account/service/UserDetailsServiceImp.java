package account.service;

import account.controller.dto.ChangeAccessDto;
import account.controller.dto.ChangeRoleDto;
import account.exception.*;
import account.model.*;
import account.repository.UserRepository;
import account.security.AuthenticationEntryPointImp;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final AuthenticationEntryPointImp authenticationEntryPoint;

    private final short MINIMUM_PASSWORD_LENGTH = 12;

    @Autowired
    public UserDetailsServiceImp(UserRepository userRepository,
                                 AuthenticationEntryPointImp authenticationEntryPoint) {
        this.userRepository = userRepository;
        this.authenticationEntryPoint = authenticationEntryPoint;
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
        if (user.isLockedAccount()) {
            authenticationEntryPoint.throwLockedException();
        }
        return new UserDetailsImp(user);
    }

    public User getUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email);
        if (user == null) {
            throw new UsernameNotFoundException("User '" + email + "' does not exist");
        }
        return user;
    }

    @Transactional
    public User signup(User user, PasswordEncoder encoder) {
        if (userExist(user.getEmail())) {
            throw new UserExistException();
        }
        validatePassword(user.getPassword());
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(encoder.encode(user.getPassword()));
        user.setLockedAccount(false);
        assertRole(user);
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

    private void assertRole(User user) {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            user.addRole(Role.ROLE_ADMINISTRATOR);
        } else {
            user.addRole(Role.ROLE_USER);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String email) {
        User userToDelete = userRepository.findByEmailIgnoreCase(email);
        if (userToDelete == null) {
            throw new UserDoesNotExistException();
        }
        if (userToDelete.isAdmin()) {
            throw new AdministratorRemoveException();
        }
        userRepository.delete(userToDelete);
    }

    @Transactional
    public User changeUserRole(ChangeRoleDto changeRoleDto) {
        User user = userRepository.findByEmailIgnoreCase(changeRoleDto.getUser());
        validateUser(user);
        Role role = validateRole(changeRoleDto, user);
        if (changeRoleDto.getOperation().equals(ChangeRoleOperation.GRANT)) {
            user.addRole(role);
        } else {
            user.removeRole(role);
        }
        return user;
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserDoesNotExistException();
        }
    }

    private Role validateRole(ChangeRoleDto changeRoleDto, User user) {
        Role role;
        try {
            role = Role.valueOf("ROLE_" + changeRoleDto.getRole().toUpperCase());
        } catch (Exception e) {
            throw new RoleDoesNotExistException();
        }
        if (!user.hasRole(role) && changeRoleDto.getOperation().equals(ChangeRoleOperation.REMOVE)) {
            throw new UserDoesNotHaveRoleException();
        }
        if (role.equals(Role.ROLE_ADMINISTRATOR) && changeRoleDto.getOperation().equals(ChangeRoleOperation.REMOVE)) {
            throw new AdministratorRemoveException();
        }
        if (user.getRoles().size() <= 1 && changeRoleDto.getOperation().equals(ChangeRoleOperation.REMOVE)) {
            throw new LastRoleDeletionException();
        }
        checkForRoleCombination(changeRoleDto, role, user);
        return role;
    }

    private void checkForRoleCombination(ChangeRoleDto changeRoleDto, Role role, User user) {
        if (changeRoleDto.getOperation().equals(ChangeRoleOperation.GRANT)) {
            if (user.isAdmin() && role.isBusiness()) {
                throw new AdministrativeAndBusinessRoleCombinationException();
            }
            if (user.isBusiness() && role.isAdmin()) {
                throw new AdministrativeAndBusinessRoleCombinationException();
            }
        }
    }

    public void changeUserAccess(ChangeAccessDto accessDto) {
        User user = userRepository.findByEmailIgnoreCase(accessDto.getUser());
        boolean lock = accessDto.getOperation().equals(AccessOperation.LOCK);
        if (user.isAdmin() && lock) {
            throw new AdministratorLockException();
        }
        user.setLockedAccount(lock);
    }
}
