package account.controller;

import account.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/empl")
public class EmployeeController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payment")
    public ResponseEntity<?> getPayments(@RequestParam(required = false) String period,
                                         @AuthenticationPrincipal UserDetails user) {
        if (period != null) {
            return ResponseEntity.ok(paymentService.getPaymentForPeriodAndEmployee(period, user.getUsername()));
        }
        return ResponseEntity.ok(paymentService.getPaymentsForEmployee(user.getUsername()));
    }
}
