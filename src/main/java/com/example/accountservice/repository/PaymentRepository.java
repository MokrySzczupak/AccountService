package com.example.accountservice.repository;

import com.example.accountservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment getPaymentByEmployee_EmailIgnoreCaseAndPeriod(String employee, YearMonth period);
    List<Payment> getPaymentsByEmployee_EmailOrderByPeriodDesc(String email);
}
