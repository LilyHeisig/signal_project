package com.alerts.alert_strategies;

import java.util.List;
import java.util.ArrayList;
import com.alerts.BloodPressureAlertFactory;
import com.data_management.PatientRecord;
import com.alerts.Alert;
/**
 * The {@code BloodPressureAlertStrategy} class is used to check for alerts related to blood pressure readings.
 */
public class BloodPressureAlertStrategy implements AlertStrategy{
    private BloodPressureAlertFactory AlertFactory;
    
    public BloodPressureAlertStrategy() {
        this.AlertFactory = new BloodPressureAlertFactory();
    }
    @Override
    public Alert checkAlert(List<PatientRecord> patientRecords) {
        List<List<PatientRecord>> filteredRecords = filterRecords(patientRecords);
        if (isBloodPressureCritical(filteredRecords)) {
            return createAlert(patientRecords.get(0).getPatientId(), patientRecords.get(patientRecords.size()-1).getTimestamp());
        }
        return null;
    }

    private List<List<PatientRecord>> filterRecords(List<PatientRecord> patientRecords) {
        // Initialize lists to store filtered records
        List<PatientRecord> systolicPressure = new ArrayList<PatientRecord>();
        List<PatientRecord> diastolicPressure = new ArrayList<PatientRecord>();
        // Logic to filter records
        for (int i = 0; i < patientRecords.size(); i++) {
            PatientRecord patientRecord = patientRecords.get(i);
            if (patientRecord.getRecordType().equals("SystolicPressure")) {
                systolicPressure.add(patientRecord);
            } else if (patientRecord.getRecordType().equals("DiastolicPressure")) {
                diastolicPressure.add(patientRecord);
            }
        }
        // Put the two lists into a list of lists and return it
        List<List<PatientRecord>> filteredRecords = new ArrayList<List<PatientRecord>>();
        filteredRecords.add(systolicPressure);
        filteredRecords.add(diastolicPressure);
        return filteredRecords;
    }

    /**
     * Determines two types of alerts based on the patient's blood pressure readings:
     * 
     * Trend Alert:
     *      Trigger an alert if the patient's blood pressure (systolic or diastolic)
     *      shows a consistent increase or decrease across three consecutive readings
     *       where each reading changes by more than 10 mmHg from the last.
     * Critical Threshold Alert:
     *      Trigger an alert if the systolic blood pressure exceeds 180 mmHg or drops
     *      below 90 mmHg, or if diastolic blood pressure exceeds 120 mmHg or drops
     *      below 60 mmHg.
     * @param filteredRecords the patient's blood pressure readings in a list of list of PatientRecords
     * @return True if the patient's blood pressure readings are critical, false otherwise
     */
    private boolean isBloodPressureCritical(List<List<PatientRecord>> filteredRecords) {
        // Trend alert
        for (List<PatientRecord> pressure : filteredRecords) {
            if (pressure.isEmpty() == true) {
                continue;
            }
            for (int i = 0; i < pressure.size() - 2; i++) {
                if (Math.abs(pressure.get(i).getMeasurementValue() - pressure.get(i + 1).getMeasurementValue()) > 10
                        && Math.abs(pressure.get(i + 1).getMeasurementValue() - pressure.get(i + 2).getMeasurementValue()) > 10) {
                    return true; //might also wrongly return an alert if the values are decreasing first, then increasing or vice versa
                }
            }
        }
        // Critical threshold alert
        List<PatientRecord> systolicPressure = filteredRecords.get(0);
        List<PatientRecord> diastolicPressure = filteredRecords.get(1);
        if (systolicPressure.isEmpty() == false
                && (systolicPressure.get(systolicPressure.size()-1).getMeasurementValue() > 180
                || systolicPressure.get(systolicPressure.size()-1).getMeasurementValue() < 90)) {
            return true;
        } else if(diastolicPressure.isEmpty() == false
                && (diastolicPressure.get(diastolicPressure.size()-1).getMeasurementValue() > 120
                || diastolicPressure.get(diastolicPressure.size()-1).getMeasurementValue() < 60)) {
            return true;
        }
        // if no conditions are met
        return false;
    }

    private Alert createAlert(int patientId, long timestamp) {
        return AlertFactory.createAlert(patientId, timestamp);
    }

}
