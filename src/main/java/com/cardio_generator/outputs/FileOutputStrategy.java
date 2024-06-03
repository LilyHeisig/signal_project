package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

public class FileOutputStrategy implements OutputStrategy { // Class names should be in CamelCase

    private String baseDirectory; // Changed variable name to camelCase

    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>(); // Changed variable name to camelCase

    /** //added another star here to match Javadoc style
     * Constructor for the FileOutputStrategy class.
     */
    public FileOutputStrategy(String baseDirectory) { // Changed class name to CamelCase
        this.baseDirectory = baseDirectory; // Changed variable name to camelCase
    }

    /**
     * Outputs the patient data to a file.
     *
     * @param patientId The ID of the patient
     * @param timestamp The timestamp of the data
     * @param label     The label of the data
     * @param data      The data to be output
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory)); // Changed variable name to camelCase
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the filePath variable
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString()); // Changed variable name to camelCase

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) { // Changed variable name to camelCase
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage()); // Changed variable name to camelCase
        }
    }
}
