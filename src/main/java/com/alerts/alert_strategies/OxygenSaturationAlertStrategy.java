package com.alerts.alert_strategies;

import java.util.List;
import com.data_management.PatientRecord;
import com.alerts.Alert;
import com.alerts.BloodOxygenAlertFactory;
import java.util.ArrayList;

public class OxygenSaturationAlertStrategy implements AlertStrategy{
    private BloodOxygenAlertFactory alertFactory;

    public OxygenSaturationAlertStrategy() {
        this.alertFactory = new BloodOxygenAlertFactory();
    }

    @Override
    public Alert checkAlert(List<PatientRecord> patientRecords) {
        List<PatientRecord> filteredRecords = filterRecords(patientRecords);
        if (isBloodSaturationCritical(filteredRecords)) {
            return createAlert(patientRecords.get(0).getPatientId(), patientRecords.get(patientRecords.size()-1).getTimestamp());
        }
        return null;
    }

    public List<PatientRecord> filterRecords(List<PatientRecord> patientRecords) {
        // Initialize lists to store filtered records
        List<PatientRecord> bloodOxygenSaturation = new ArrayList<PatientRecord>();
        // Logic to filter records
        for (int i = 0; i < patientRecords.size(); i++) {
            PatientRecord patientRecord = patientRecords.get(i);
            if (patientRecord.getRecordType().equals("Saturation")) {
                bloodOxygenSaturation.add(patientRecord);
            }
        }
        return bloodOxygenSaturation;
    }

    /**
     * Determines if the patient's blood saturation data is critical. Two scenarios are detected:
     *      Low Saturation Alert:
     *          Trigger an alert if the blood oxygen saturation level falls below 92%.
     *      Rapid Drop Alert:
     *          Trigger an alert if the blood oxygen saturation level drops by 5% or more
     *          within a 10-minute interval.
     * 
     * @param saturation the patient's blood saturation data
     */
    private boolean isBloodSaturationCritical(List<PatientRecord> saturation) {
        if (saturation.isEmpty() == true) {
            return false;
        }
        // Low saturation alert
        if (saturation.get(saturation.size()-1).getMeasurementValue() < 92) {
            return true;
        }
        // Rapid drop alert
        for (int i = 0; i < saturation.size() - 1; i++) {
            for (int j = i + 1; j < saturation.size(); j++) {
                // only check saturation if the timestamps are within a 10 minute interval
                if (saturation.get(j).getTimestamp() - saturation.get(i).getTimestamp() < 600000) {
                    break;
                }
                // if the saturation drops by 5% or more (within a 10-minute interval)
                if (saturation.get(i).getMeasurementValue() - saturation.get(j).getMeasurementValue() >= 5) {
                    return true;
                }
            }
        }
        return false;
    }

    private Alert createAlert(int patientId, long timestamp) {
        return alertFactory.createAlert(patientId, timestamp);
    }
}
