package com.cardio_generator.outputs;

/**
 * Interface for output strategies.
 */
public interface OutputStrategy {
    void output(int patientId, long timestamp, String label, String data);
}
