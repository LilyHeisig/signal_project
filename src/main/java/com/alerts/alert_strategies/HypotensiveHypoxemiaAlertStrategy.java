package com.alerts.alert_strategies;

import java.util.ArrayList;
import java.util.List;
import com.data_management.PatientRecord;   
import com.alerts.Alert;
import com.alerts.HypotensiveHypoxemiaAlertFactory;

public class HypotensiveHypoxemiaAlertStrategy implements AlertStrategy{
    private HypotensiveHypoxemiaAlertFactory alertFactory;

    public HypotensiveHypoxemiaAlertStrategy() {
        this.alertFactory = new HypotensiveHypoxemiaAlertFactory();
    }

    @Override
    public Alert checkAlert(List<PatientRecord> patientRecords) {
        List<List<PatientRecord>> filteredRecords = filterRecords(patientRecords);
        List<PatientRecord> saturation = filteredRecords.get(0);
        List<PatientRecord> systolicPressure = filteredRecords.get(1);
        if (isThereHypotensiveHypoxemia(saturation, systolicPressure)) {
            return createAlert(patientRecords.get(0).getPatientId(), patientRecords.get(patientRecords.size()-1).getTimestamp());
        }
        return null;
    }

    private List<List<PatientRecord>> filterRecords(List<PatientRecord> patientRecords) {
        // Initialize lists to store filtered records
        List<PatientRecord> saturation = new ArrayList<PatientRecord>();
        List<PatientRecord> diastolicPressure = new ArrayList<PatientRecord>();

        // Logic to filter records
        for (int i = 0; i < patientRecords.size(); i++) {
            PatientRecord patientRecord = patientRecords.get(i);
            if (patientRecord.getRecordType().equals("Saturation")) {
                saturation.add(patientRecord);
            } else if (patientRecord.getRecordType().equals("SystolicPressure")) {
                diastolicPressure.add(patientRecord);
            }
        }
        // Put the two lists into a list of lists and return it
        List<List<PatientRecord>> filteredRecords = new ArrayList<List<PatientRecord>>();
        filteredRecords.add(saturation);
        filteredRecords.add(diastolicPressure);
        return filteredRecords;
    }

    /**
     * Determines if the patient has hypotensive hypoxemia.
     * The alert should trigger when both:
     *      - Systolic blood pressure is below 90 mmHg.
     *      - Blood oxygen saturation falls below 92%.
     */
    private boolean isThereHypotensiveHypoxemia(List<PatientRecord> saturation, List<PatientRecord> systolicPressure) {
        if (systolicPressure.isEmpty() == true || saturation.isEmpty() == true) {
            return false;
        }
        if (systolicPressure.get(systolicPressure.size()-1).getMeasurementValue() < 90
                && saturation.get(saturation.size()-1).getMeasurementValue() < 92) {
            return true;
        }
        return false;
    }

    private Alert createAlert(int patientId, long timestamp) {
        return alertFactory.createAlert(patientId, timestamp);
    }
}
