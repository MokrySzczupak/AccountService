package account.controller.dto;

import account.model.AccessOperation;
import lombok.Data;

@Data
public class ChangeAccessDto {
    private String user;
    private AccessOperation operation;
}
