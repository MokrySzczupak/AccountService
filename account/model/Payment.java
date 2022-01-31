package account.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.YearMonth;

@Entity
public class Payment {
    @Transient
    public String getLastname() {
        return employee.getLastname();
    }

    @Transient
    public String getName() {
        return employee.getName();
    }

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
    private User employee;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPeriod() {
        String month = period.getMonth().name();
        String monthProperFormat = month.charAt(0) + month.substring(1).toLowerCase();
        return monthProperFormat + "-" + period.getYear();
    }

    public void setPeriod(YearMonth period) {
        this.period = period;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
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
