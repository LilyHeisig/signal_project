package com.cardio_generator.generators;

// no blank lines between import statements, statements in ASCII sort order
import com.cardio_generator.outputs.OutputStrategy;
import java.util.Random;

public class AlertGenerator implements PatientDataGenerator {

    // Constants should be in upper case with words separated by underscores
    public static final Random RANDOM_GENERATOR = new Random(); // Changed to comply with naming convention for constants
    
    private boolean[] alertStates; // Changed variable name to comply with camelCase convention

    /**
     * Constructor for the AlertGenerator class.
     *
     * @param patientCount The number of patients
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1]; // Changed variable name to camelCase
    }

    /**
     * Generates alert data for a patient. Alerts are triggered randomly with a given average rate and resolved with a high probability.
     *
     * @param patientId      The ID of the patient
     * @param outputStrategy The output strategy to use
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) { // Changed variable name to camelCase
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // Changed to use the constant name
                    alertStates[patientId] = false; // Changed variable name to camelCase
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double lambda = 0.1; // Variable names should be in camelCase
                double p = -Math.expm1(-lambda); // Variable names should be in camelCase
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p; // Changed to use the constant name

                if (alertTriggered) {
                    alertStates[patientId] = true; // Changed variable name to camelCase
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
