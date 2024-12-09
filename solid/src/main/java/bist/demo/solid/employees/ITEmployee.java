package bist.demo.solid.employees;

import bist.demo.solid.Employee;
import bist.demo.solid.SalaryCalculator;
import bist.demo.solid.Worker;

public class ITEmployee extends Employee implements SalaryCalculator, Worker {

    public ITEmployee(String name) {
        super(name, "IT");
    }

    @Override
    public void calculateSalary() {
        System.out.println(getName() + " calculates salaries for IT.");
    }

    @Override
    public void work() {
        System.out.println(getName() + " is coding in the IT department.");
    }
}
