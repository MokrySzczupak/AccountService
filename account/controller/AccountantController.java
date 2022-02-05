package account.controller;

import account.controller.dto.PaymentDto;
import account.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("api/acct")
public class AccountantController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payments")
    public Map<String, String> postPayments(@RequestBody List<PaymentDto> payments) {
        paymentService.savePayments(payments);
        return new ConcurrentHashMap<>(Map.of("status", "Added successfully!"));
    }

    @PutMapping("/payments")
    public Map<String, String> updatePayment(@RequestBody PaymentDto payment) {
        paymentService.updatePayment(payment);
        return new ConcurrentHashMap<>(Map.of("status", "Updated successfully!"));
    }
}
