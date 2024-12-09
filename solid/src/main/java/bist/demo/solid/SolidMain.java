package bist.demo.solid;


import bist.demo.solid.employees.FinanceEmployee;
import bist.demo.solid.employees.HREmployee;
import bist.demo.solid.employees.ITEmployee;

public class SolidMain {

    public static void main(String[] args) {
        Employee employee1 = new HREmployee("Alice");
        Employee employee2 = new ITEmployee("Bob");
        Employee employee3 = new FinanceEmployee("Charlie");

        ((Worker) employee1).work();
        ((Worker) employee2).work();
        ((Worker) employee3).work();

        ((SalaryCalculator) employee1).calculateSalary();
        ((SalaryCalculator) employee2).calculateSalary();
        ((SalaryCalculator) employee3).calculateSalary();
    }
}
