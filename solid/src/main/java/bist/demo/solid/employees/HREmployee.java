package bist.demo.solid.employees;

import bist.demo.solid.Employee;
import bist.demo.solid.SalaryCalculator;
import bist.demo.solid.Worker;

public class HREmployee extends Employee implements SalaryCalculator, Worker {

    public HREmployee(String name) {
        super(name, "HR");
    }

    @Override
    public void calculateSalary() {
        System.out.println(getName() + " calculates salaries for HR.");
    }

    @Override
    public void work() {
        System.out.println(getName() + " is handling HR tasks.");
    }
}
