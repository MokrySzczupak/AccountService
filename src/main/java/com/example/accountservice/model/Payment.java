package com.example.accountservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.YearMonth;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private YearMonth period;
    @NotNull
    private String salary;
    @NotNull
    @ManyToOne
    @JsonBackReference
    @JsonIgnore
    private User employee;

    @Transient
    public String getLastname() {
        return employee.getLastname();
    }

    @Transient
    public String getName() {
        return employee.getName();
    }

    public String getPeriod() {
        String month = period.getMonth().name();
        String monthProperFormat = month.charAt(0) + month.substring(1).toLowerCase();
        return monthProperFormat + "-" + period.getYear();
    }

    public String calculateSalary(long salary) {
        long cents = salary % 100;
        long dollars = salary / 100;
        return String.format("%d dollar(s) %d cent(s)", dollars, cents);
    }

    @JsonIgnore
    public YearMonth getYearMonthPeriod() {
        return period;
    }
}
