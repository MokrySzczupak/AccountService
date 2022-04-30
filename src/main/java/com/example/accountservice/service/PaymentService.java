package com.example.accountservice.service;

import com.example.accountservice.controller.dto.PaymentDto;
import com.example.accountservice.model.Payment;
import com.example.accountservice.model.User;
import com.example.accountservice.model.UserDetailsImp;
import com.example.accountservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserDetailsServiceImp userDetailsService;

    @Transactional
    public void savePayments(List<PaymentDto> payments) {
        for (PaymentDto paymentDto: payments) {
            User employee = ((UserDetailsImp) userDetailsService.loadUserByUsername(paymentDto.getEmployee())).getUser();
            savePaymentForEmployee(paymentDto, employee);
        }
    }

    private void savePaymentForEmployee(PaymentDto paymentDto, User employee) {
        if (employee != null) {
            Payment payment = new Payment();
            payment.setEmployee(employee);
            YearMonth period = validatePaymentAndGenerateProperDate(paymentDto);
            payment.setPeriod(period);
            checkIfPaymentAlreadyExist(payment);
            payment.setSalary(payment.calculateSalary(paymentDto.getSalary()));
            paymentRepository.save(payment);
        }
    }

    private YearMonth validatePaymentAndGenerateProperDate(PaymentDto paymentDto) {
        YearMonth period = validateAndGenerateProperPeriod(paymentDto.getPeriod());
        if (paymentDto.getSalary() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary must be non negative!");
        }
        return period;
    }

    private YearMonth validateAndGenerateProperPeriod(String period) {
        String[] periodSplit = period.split("-");
        int month = Integer.parseInt(periodSplit[0]);
        int year = Integer.parseInt(periodSplit[1]);
        if (month > 12 || month < 0 || year < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date!");
        }
        return YearMonth.of(year, month);
    }

    private void checkIfPaymentAlreadyExist(Payment payment) {
        Payment existingPayment = paymentRepository.getPaymentByEmployee_EmailIgnoreCaseAndPeriod(payment.getEmployee().getEmail(), payment.getYearMonthPeriod());
        if (existingPayment != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment exist!");
        }
    }

    @Transactional
    public void updatePayment(PaymentDto paymentDto) {
        YearMonth period = validatePaymentAndGenerateProperDate(paymentDto);
        Payment payment = paymentRepository.getPaymentByEmployee_EmailIgnoreCaseAndPeriod(paymentDto.getEmployee(), period);
        payment.setSalary(payment.calculateSalary(paymentDto.getSalary()));
    }

    @Transactional
    public List<Payment> getPaymentsForEmployee(String email) {
        List<Payment> payments = paymentRepository.getPaymentsByEmployee_EmailOrderByPeriodDesc(email);
        return payments == null ? new ArrayList<>() : payments;
    }

    public Payment getPaymentForPeriodAndEmployee(String periodStr, String employee) {
        YearMonth period = validateAndGenerateProperPeriod(periodStr);
        return paymentRepository.getPaymentByEmployee_EmailIgnoreCaseAndPeriod(employee, period);
    }

}
