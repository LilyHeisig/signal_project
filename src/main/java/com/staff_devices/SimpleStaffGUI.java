package com.staff_devices;

import javax.swing.*;
import java.awt.*;
import com.alerts.Alert;
/**
 * The {@code SimpleStaffGUI} class is used to display alerts in a simple GUI.
 */
public class SimpleStaffGUI implements StaffDevice {
    private JFrame frame;
    private JTextArea textArea;

    public SimpleStaffGUI() {
        SwingUtilities.invokeLater(() -> {
            // Create the frame
            frame = new JFrame("Alert");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 300);

            // Create a text area to display alerts
            textArea = new JTextArea();
            textArea.setFont(new Font("Arial", Font.PLAIN, 16));
            textArea.setEditable(false);

            // Wrap the text area in a scroll pane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            // Add the scroll pane to the frame
            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

            // Make the frame visible
            frame.setVisible(true);
        });
    }

    @Override
    public void displayAlert(Alert alert) {
        SwingUtilities.invokeLater(() -> {
            // Prepare text to be displayed
            String text = alert.getMessage() + "\n";

            // Append the new alert text to the text area
            textArea.append(text);
        });
    }
}
