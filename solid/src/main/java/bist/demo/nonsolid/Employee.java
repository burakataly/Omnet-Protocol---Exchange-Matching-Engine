package bist.demo.nonsolid;

public class Employee {

    private final String name;
    private final String department;

    public Employee(String name, String department) {
        this.name = name;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public void work() {
        if (department.equalsIgnoreCase("HR")) {
            System.out.println(name + " is handling HR tasks.");
        } else if (department.equalsIgnoreCase("IT")) {
            System.out.println(name + " is coding in the IT department.");
        } else if (department.equalsIgnoreCase("Finance")) {
            System.out.println(name + " is managing finances.");
        }
    }

    public void calculateSalary() {
        if (department.equalsIgnoreCase("HR")) {
            System.out.println(name + " calculates salaries for HR.");
        } else if (department.equalsIgnoreCase("IT")) {
            System.out.println(name + " calculates salaries for IT.");
        } else if (department.equalsIgnoreCase("Finance")) {
            System.out.println(name + " calculates salaries for Finance.");
        }
    }
}
