package account.controller.dto;

import lombok.Data;

@Data
public class PaymentDto {
    private String employee;
    private long salary;
    private String period;
}
