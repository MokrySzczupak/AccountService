package account.controller;

import account.model.User;
import account.service.PaymentService;
import account.service.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/empl")
public class EmployeeController {

    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payment")
    public ResponseEntity<?> getPayments(@RequestParam(required = false) String period,
                                      Authentication auth) {
        User user = userDetailsServiceImp.loadLoggedUser(auth);
        if (period != null) {
            return ResponseEntity.ok(paymentService.getPaymentForPeriodAndEmployee(period, user.getEmail()));
        }
        return ResponseEntity.ok(paymentService.getPaymentsForEmployee(user.getEmail()));
    }
}
