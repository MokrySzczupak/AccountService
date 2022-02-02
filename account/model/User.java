package account.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String lastname;
    @NotEmpty
    @Pattern(regexp = "\\w+(@acme.com)$")
    private String email;
    @NotEmpty
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String password;
    @OneToMany(mappedBy = "employee")
    private List<Payment> payments;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public List<Role> getRoles() {
        Collections.sort(roles);
        return roles;
    }

    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMINISTRATOR);
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean isBusiness() {
        return roles.contains(Role.ROLE_ACCOUNTANT) || roles.contains(Role.ROLE_USER);
    }
}
