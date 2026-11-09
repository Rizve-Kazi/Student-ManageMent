package com.example.studentmanager.util;

import com.example.studentmanager.model.Student;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * resources/web/report.html এবং style.css থেকে HTML রিপোর্ট তৈরি করে।
 */
public class HtmlReport {

    private HtmlReport() {
    }

    public static String build(List<Student> students) {
        try {
            String template = load("/web/report.html");
            String css = load("/web/style.css");

            Map<String, Integer> byDept = new TreeMap<>();
            for (Student s : students) {
                String d = blank(s.getDepartment()) ? "(none)" : s.getDepartment();
                byDept.merge(d, 1, Integer::sum);
            }

            StringBuilder deptRows = new StringBuilder();
            int i = 0;
            for (Map.Entry<String, Integer> e : byDept.entrySet()) {
                deptRows.append("<tr").append(rowClass(i++)).append("><td>")
                        .append(esc(e.getKey())).append("</td><td>")
                        .append(e.getValue()).append("</td></tr>\n");
            }
            if (byDept.isEmpty()) {
                deptRows.append("<tr><td colspan=\"2\" class=\"empty\">No data</td></tr>");
            }

            StringBuilder rows = new StringBuilder();
            i = 0;
            for (Student s : students) {
                rows.append("<tr").append(rowClass(i++)).append(">")
                        .append("<td>").append(s.getId()).append("</td>")
                        .append("<td>").append(esc(s.getName())).append("</td>")
                        .append("<td>").append(esc(s.getRoll())).append("</td>")
                        .append("<td>").append(esc(s.getDepartment())).append("</td>")
                        .append("<td>").append(esc(s.getPhone())).append("</td>")
                        .append("</tr>\n");
            }
            if (students.isEmpty()) {
                rows.append("<tr><td colspan=\"5\" class=\"empty\">No students yet</td></tr>");
            }

            return template
                    .replace("{{css}}", css)
                    .replace("{{date}}", LocalDate.now().toString())
                    .replace("{{total}}", String.valueOf(students.size()))
                    .replace("{{deptCount}}", String.valueOf(byDept.size()))
                    .replace("{{deptRows}}", deptRows.toString())
                    .replace("{{studentRows}}", rows.toString());
        } catch (IOException e) {
            return "<html><body>Could not load report template: " + esc(e.getMessage()) + "</body></html>";
        }
    }

    private static String rowClass(int index) {
        return index % 2 == 1 ? " class=\"alt\"" : "";
    }

    private static boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String load(String path) throws IOException {
        try (InputStream in = HtmlReport.class.getResourceAsStream(path)) {
            if (in == null) throw new IOException("Resource not found: " + path);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /** HTML escape, যাতে নামে < > & থাকলে রিপোর্ট ভেঙে না যায়। */
    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
