package com.example.studentmanager.ui;

import com.example.studentmanager.model.Student;
import com.example.studentmanager.util.HtmlReport;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/** HTML + CSS দিয়ে সাজানো রিপোর্ট ট্যাব। */
public class ReportPanel extends JPanel {
    private final JEditorPane view = new JEditorPane();
    private List<Student> current = new ArrayList<>();

    public ReportPanel(Runnable onRefresh) {
        super(new BorderLayout());

        view.setContentType("text/html");
        view.setEditable(false);

        JButton refreshBtn = new JButton("Refresh");
        JButton exportBtn = new JButton("Export HTML");
        refreshBtn.addActionListener(e -> onRefresh.run());
        exportBtn.addActionListener(e -> exportHtml());

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.add(refreshBtn);
        bar.add(exportBtn);

        add(bar, BorderLayout.NORTH);
        add(new JScrollPane(view), BorderLayout.CENTER);
    }

    public void showStudents(List<Student> students) {
        this.current = new ArrayList<>(students);
        view.setText(HtmlReport.build(current));
        view.setCaretPosition(0);
    }

    private void exportHtml() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("student-report.html"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try {
            Files.writeString(file.toPath(), HtmlReport.build(current), StandardCharsets.UTF_8);
            int open = JOptionPane.showConfirmDialog(this,
                    "Saved: " + file.getAbsolutePath() + "\nOpen in browser?",
                    "Exported", JOptionPane.YES_NO_OPTION);
            if (open == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(file.toURI());
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
