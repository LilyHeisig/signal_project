package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.staff_devices.SimpleStaffGUI;
import com.staff_devices.StaffDevice;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private AlertPublisher alertPublisher;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.alertPublisher = AlertPublisher.getInstance();
        StaffDevice device = new SimpleStaffGUI();
        alertPublisher.subscribe(device);
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient, List<PatientRecord> patientRecords) throws Exception {
        System.out.println("AlertGenerator got data. Evaluating...");
        if (patientRecords.isEmpty() == true) {
            return;
        }
        // filter the patient records for the specific record types
        List<PatientRecord> systolicPressure = new ArrayList<PatientRecord>();
        List<PatientRecord> diastolicPressure = new ArrayList<PatientRecord>();
        List<PatientRecord> saturation = new ArrayList<PatientRecord>();
        List<PatientRecord> ecgData = new ArrayList<PatientRecord>();

        System.out.println("sorting patient records");
        for (int i = 0; i < patientRecords.size(); i++) {
            PatientRecord patientRecord = patientRecords.get(i);
            if (patientRecord.getRecordType().equals("SystolicPressure")) {
                systolicPressure.add(patientRecord);
            } else if (patientRecord.getRecordType().equals("DiastolicPressure")) {
                diastolicPressure.add(patientRecord);
            } else if (patientRecord.getRecordType().equals("Saturation")) {
                saturation.add(patientRecord);
            } else if (patientRecord.getRecordType().equals("ECGData")) {
                ecgData.add(patientRecord);
            }
            else {
                throw new Exception("Unknown record type: " + patientRecord.getRecordType());
            }
        }
        // get the current time
        long currentTime = System.currentTimeMillis();
        // get the patient id
        int patientId = patient.getPatientId();
        // Check for critical conditions
        System.out.println("checking for critical conditions");
        if (isBloodPressureCritical(systolicPressure, diastolicPressure)) {
            triggerAlert(new Alert(patientId, "BloodPressure", currentTime));
        }
        if (isBloodSaturationCritical(saturation)) {
            triggerAlert(new Alert(patientId, "BloodSaturation", currentTime));
        }
        if (isECGDataCritical(ecgData)) {
            triggerAlert(new Alert(patientId, "WeirdECGData", currentTime));
        }
        if (isThereHypotensiveHypoxemia(saturation, systolicPressure)) {
            triggerAlert(new Alert(patientId, "HypotensiveHypoxemia", currentTime));
        }
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
     * @param The patient's blood pressure readings
     * @return
     */
    private boolean isBloodPressureCritical(List<PatientRecord> systolicPressure, List<PatientRecord> diastolicPressure) {
        // Trend alert
        List<List<PatientRecord>> pressure_list = Arrays.asList(systolicPressure, diastolicPressure);
        for (List<PatientRecord> pressure : pressure_list) {
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

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // print out the alert information
        System.out.println("Alert triggered for patient " + alert.getPatientId());
        System.out.println("Timestamp: " + alert.getTimestamp());
        System.out.println("The critical condition is: " + alert.getCondition());
        // log the alert

        // notify staff
        alertPublisher.publishAlert(alert);
        System.out.println("notified staff device");
    }
}
