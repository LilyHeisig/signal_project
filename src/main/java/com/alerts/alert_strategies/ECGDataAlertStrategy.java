package com.alerts.alert_strategies;

import java.util.List;
import com.data_management.PatientRecord;
import com.alerts.Alert;
import com.alerts.ECGAlertFactory;
import java.util.ArrayList;

public class ECGDataAlertStrategy implements AlertStrategy{
    private ECGAlertFactory alertFactory;
    
    public ECGDataAlertStrategy() {
        this.alertFactory = new ECGAlertFactory();
    }
    @Override
    public Alert checkAlert(List<PatientRecord> patientRecords) {
        List<PatientRecord> ecgData = filterRecords(patientRecords);
        if (isECGDataCritical(ecgData)) {
            return createAlert(patientRecords.get(0).getPatientId(), patientRecords.get(patientRecords.size()-1).getTimestamp());
        }
        return null;
    }
    private Alert createAlert(int patientId, long timestamp) {
        return alertFactory.createAlert(patientId, timestamp);
    }

    private List<PatientRecord> filterRecords(List<PatientRecord> patientRecords) {
        // Initialize lists to store filtered records
        List<PatientRecord> ecgData = new ArrayList<PatientRecord>();
        // Logic to filter records
        for (int i = 0; i < patientRecords.size(); i++) {
            PatientRecord patientRecord = patientRecords.get(i);
            if (patientRecord.getRecordType().equals("ECGData")) {
                ecgData.add(patientRecord);
            }
        }
        return ecgData;
    }

    /**
     * ECG Data Alerts find abnormal data.
     * This means: Trigger an alert if peaks above certain values happen.
     *      Measure the average data generated using a sliding window. Then if any
     *      peaks occur far beyond the current average generate an alert.
     * @param alert
     */

     private boolean isECGDataCritical(List<PatientRecord> ecgData) {
        // need at least 3 data points to compute the average and standard deviation
        // between the first two points and compare it to the third
        if (ecgData.size() < 3) { 
            return false;
        }
        // compute the average of the ecgData measurement values leaving out the last element
        double average = ecgData.stream().limit(ecgData.size() - 1).mapToDouble(PatientRecord::getMeasurementValue).average()
                .orElse(0);
        // compute the standard deviation of the ecdData leaving out the last element
        double standardDeviation = Math.sqrt(ecgData.stream().limit(ecgData.size() - 1)
                .mapToDouble(PatientRecord::getMeasurementValue).map(x -> Math.pow(x - average, 2)).average().orElse(0));
        // check if the last element is 3 standard deviations beyond the average 
        // (so the data comes from the normal distribution with 99.7% confidence interval)
        double standardDeviationsBeyondAverage = 3;
        if (ecgData.get(ecgData.size() - 1).getMeasurementValue() > average + standardDeviationsBeyondAverage * standardDeviation
                || ecgData.get(ecgData.size() - 1).getMeasurementValue() < average - standardDeviationsBeyondAverage * standardDeviation){
            return true;
        }
        return false;
    }
}
