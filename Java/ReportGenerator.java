import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportGenerator {

    static final String[] HABITS = {"water", "exercise", "read", "sleep", "meditate"};
    static final String DB_PATH = "../Database/habits.json";
    static final String REPORT_PATH = "../Database/WeeklyReport.txt";

    public static void main(String[] args) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject data;

        try (FileReader reader = new FileReader(DB_PATH)) {
            data = (JSONObject) parser.parse(reader);
        } catch (FileNotFoundException e) {
            System.out.println("habits.json not found. Save some habits first.");
            return;
        }

        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("       WEEKLY HABIT REPORT\n");
        report.append("  Generated: ").append(today).append("\n");
        report.append("========================================\n\n");

        // Daily summary
        report.append("Daily Summary (Last 7 Days):\n");
        report.append("----------------------------------------\n");

        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            String key = day.format(fmt);
            String label = day.format(DateTimeFormatter.ofPattern("EEE, MMM dd"));

            JSONObject dayData = (JSONObject) data.getOrDefault(key, new JSONObject());
            int count = 0;
            for (String habit : HABITS) {
                if (Boolean.TRUE.equals(dayData.get(habit))) count++;
            }

            String bar = "█".repeat(count) + "░".repeat(HABITS.length - count);
            report.append(String.format("%-18s [%s]  %d/%d\n", label, bar, count, HABITS.length));
        }

        // Per habit stats
        report.append("\n----------------------------------------\n");
        report.append("Habit Completion % (Last 7 Days):\n");
        report.append("----------------------------------------\n");

        for (String habit : HABITS) {
            int done = 0;
            for (int i = 0; i < 7; i++) {
                String key = today.minusDays(i).format(fmt);
                JSONObject dayData = (JSONObject) data.getOrDefault(key, new JSONObject());
                if (Boolean.TRUE.equals(dayData.get(habit))) done++;
            }
            int pct = (done * 100) / 7;
            String bar = "█".repeat(done) + "░".repeat(7 - done);
            report.append(String.format("%-12s [%s]  %d%%\n",
                capitalize(habit), bar, pct));
        }

        report.append("\n========================================\n");

        // Print to console
        System.out.println(report);

        // Save to file
        try (FileWriter fw = new FileWriter(REPORT_PATH)) {
            fw.write(report.toString());
        }
        System.out.println("Report saved to: " + REPORT_PATH);
    }

    static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
