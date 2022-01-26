package account.controller.dto;

import account.model.User;

public class UserDto {
    private final String name;
    private final String lastname;
    private final String email;

    public UserDto(User user) {
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }
}
