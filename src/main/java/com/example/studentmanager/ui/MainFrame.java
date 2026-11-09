package com.example.studentmanager.ui;

import com.example.studentmanager.db.StudentDAO;
import com.example.studentmanager.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {
    private final StudentDAO dao = new StudentDAO();

    private final JTextField nameField = new JTextField(15);
    private final JTextField rollField = new JTextField(15);
    private final JTextField deptField = new JTextField(15);
    private final JTextField phoneField = new JTextField(15);
    private final JTextField searchField = new JTextField(15);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Roll", "Department", "Phone"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final ReportPanel reportPanel = new ReportPanel(this::refreshReport);

    public MainFrame() {
        super("Student Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 580);
        setLocationRelativeTo(null);

        JPanel studentsTab = new JPanel(new BorderLayout(10, 10));
        studentsTab.add(buildFormPanel(), BorderLayout.NORTH);
        studentsTab.add(new JScrollPane(table), BorderLayout.CENTER);
        studentsTab.add(buildSearchPanel(), BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students", studentsTab);
        tabs.addTab("HTML Report", reportPanel);
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == reportPanel) refreshReport();
        });
        setContentPane(tabs);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
        });

        loadTable("");
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.anchor = GridBagConstraints.WEST;

        addRow(form, g, 0, 0, "Name:", nameField);
        addRow(form, g, 0, 2, "Roll:", rollField);
        addRow(form, g, 1, 0, "Department:", deptField);
        addRow(form, g, 1, 2, "Phone:", phoneField);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");

        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);

        g.gridx = 0;
        g.gridy = 2;
        g.gridwidth = 4;
        g.anchor = GridBagConstraints.CENTER;
        form.add(buttons, g);
        return form;
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, int col, String label, JTextField field) {
        g.gridy = row;
        g.gridwidth = 1;
        g.gridx = col;
        p.add(new JLabel(label), g);
        g.gridx = col + 1;
        p.add(field, g);
    }

    private JPanel buildSearchPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        searchBtn.addActionListener(e -> loadTable(searchField.getText().trim()));
        showAllBtn.addActionListener(e -> {
            searchField.setText("");
            loadTable("");
        });
        p.add(new JLabel("Search:"));
        p.add(searchField);
        p.add(searchBtn);
        p.add(showAllBtn);
        return p;
    }

    private void loadTable(String keyword) {
        try {
            List<Student> list = dao.search(keyword);
            model.setRowCount(0);
            for (Student s : list) {
                model.addRow(new Object[]{s.getId(), s.getName(), s.getRoll(), s.getDepartment(), s.getPhone()});
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** রিপোর্টে সবসময় সব ছাত্র দেখায় (সার্চ ফিল্টার ছাড়া)। */
    private void refreshReport() {
        try {
            reportPanel.showStudents(dao.getAll());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void fillFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        nameField.setText(String.valueOf(model.getValueAt(row, 1)));
        rollField.setText(String.valueOf(model.getValueAt(row, 2)));
        deptField.setText(String.valueOf(model.getValueAt(row, 3)));
        phoneField.setText(String.valueOf(model.getValueAt(row, 4)));
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty() || rollField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Roll are required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void addStudent() {
        if (!validateForm()) return;
        try {
            dao.add(new Student(nameField.getText().trim(), rollField.getText().trim(),
                    deptField.getText().trim(), phoneField.getText().trim()));
            clearForm();
            loadTable("");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void updateStudent() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student from the table first.");
            return;
        }
        if (!validateForm()) return;
        int id = (int) model.getValueAt(row, 0);
        try {
            dao.update(new Student(id, nameField.getText().trim(), rollField.getText().trim(),
                    deptField.getText().trim(), phoneField.getText().trim()));
            loadTable("");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student from the table first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            dao.delete((int) model.getValueAt(row, 0));
            clearForm();
            loadTable("");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void clearForm() {
        nameField.setText("");
        rollField.setText("");
        deptField.setText("");
        phoneField.setText("");
        table.clearSelection();
    }

    private void showError(SQLException ex) {
        String msg = ex.getMessage() != null && ex.getMessage().contains("UNIQUE")
                ? "This roll number already exists."
                : ex.getMessage();
        JOptionPane.showMessageDialog(this, msg, "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
