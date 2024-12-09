package bist.demo.solid.employees;

import bist.demo.solid.Employee;
import bist.demo.solid.SalaryCalculator;
import bist.demo.solid.Worker;

public class FinanceEmployee extends Employee implements SalaryCalculator, Worker {

    public FinanceEmployee(String name) {
        super(name, "Finance");
    }

    @Override
    public void calculateSalary() {
        System.out.println(getName() + " calculates salaries for Finance.");
    }

    @Override
    public void work() {
        System.out.println(getName() + " is managing finances.");
    }
}
