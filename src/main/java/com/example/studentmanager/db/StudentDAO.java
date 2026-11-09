package com.example.studentmanager.db;

import com.example.studentmanager.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void add(Student s) throws SQLException {
        String sql = "INSERT INTO students(name, roll, department, phone) VALUES(?,?,?,?)";
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getRoll());
            ps.setString(3, s.getDepartment());
            ps.setString(4, s.getPhone());
            ps.executeUpdate();
        }
    }

    public void update(Student s) throws SQLException {
        String sql = "UPDATE students SET name=?, roll=?, department=?, phone=? WHERE id=?";
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getRoll());
            ps.setString(3, s.getDepartment());
            ps.setString(4, s.getPhone());
            ps.setInt(5, s.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Student> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM students WHERE name LIKE ? OR roll LIKE ? OR department LIKE ? ORDER BY id";
        List<Student> list = new ArrayList<>();
        String k = "%" + keyword + "%";
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("roll"),
                            rs.getString("department"),
                            rs.getString("phone")));
                }
            }
        }
        return list;
    }

    public List<Student> getAll() throws SQLException {
        return search("");
    }
}
