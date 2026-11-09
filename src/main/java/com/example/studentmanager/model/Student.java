package com.example.studentmanager.model;

public class Student {
    private int id;
    private String name;
    private String roll;
    private String department;
    private String phone;

    public Student(int id, String name, String roll, String department, String phone) {
        this.id = id;
        this.name = name;
        this.roll = roll;
        this.department = department;
        this.phone = phone;
    }

    public Student(String name, String roll, String department, String phone) {
        this(0, name, roll, department, phone);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRoll() { return roll; }
    public String getDepartment() { return department; }
    public String getPhone() { return phone; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRoll(String roll) { this.roll = roll; }
    public void setDepartment(String department) { this.department = department; }
    public void setPhone(String phone) { this.phone = phone; }
}
