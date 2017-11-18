package cz.ucl.javase.xa01.company;

public class Employee {
    private String name;
    private int dailyWage;

    public Employee(String name, int dailyWage) {
        this.name = name;
        this.dailyWage = dailyWage;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDailyWage() {
        return dailyWage;
    }

    public void setDailyWage(int dailyWage) {
        this.dailyWage = dailyWage;
    }
}
