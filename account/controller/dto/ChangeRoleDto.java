package account.controller.dto;

import account.model.ChangeRoleOperation;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ChangeRoleDto {
    @NotEmpty
    private String user;
    private String role;
    private ChangeRoleOperation operation;
}
