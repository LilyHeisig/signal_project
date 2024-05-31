package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for generating patient data.
 */
public interface PatientDataGenerator {
    void generate(int patientId, OutputStrategy outputStrategy);
}
