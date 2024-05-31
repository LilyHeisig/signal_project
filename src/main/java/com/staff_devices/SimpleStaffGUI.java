package com.staff_devices;

import javax.swing.*;
import java.awt.*;

import com.alerts.Alert;
import com.staff_devices.StaffDevice;

public class SimpleStaffGUI implements StaffDevice {

    @Override
    public void displayAlert(Alert alert) {
        SwingUtilities.invokeLater(() -> {
            // Create the frame
            JFrame frame = new JFrame("Alert");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 150);

            // prepare text to be displayed
            String text = String.format("Patient Number %d has condition %s", alert.getPatientId(), alert.getCondition());

            // Create a label to display the alert text
            JLabel alertLabel = new JLabel(text, SwingConstants.CENTER);

            // Optionally set the font and padding for better display
            alertLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            alertLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Add the label to the frame
            frame.getContentPane().add(alertLabel, BorderLayout.CENTER);

            // Make the frame visible
            frame.setVisible(true);
    });
}

    
}
