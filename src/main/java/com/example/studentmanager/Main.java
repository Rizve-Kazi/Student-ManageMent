package com.example.studentmanager;

import com.example.studentmanager.db.Database;
import com.example.studentmanager.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Database.init();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new MainFrame().setVisible(true);
        });
    }
}
