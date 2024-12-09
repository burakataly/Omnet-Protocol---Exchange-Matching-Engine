package bist.demo.nonsolid;

public class NonSolidMain {

    public static void main(String[] args) {
        Employee employee1 = new Employee("Alice", "HR");
        Employee employee2 = new Employee("Bob", "IT");
        Employee employee3 = new Employee("Charlie", "Finance");

        employee1.work();
        employee2.work();
        employee3.work();

        employee1.calculateSalary();
        employee2.calculateSalary();
        employee3.calculateSalary();

    }
}
