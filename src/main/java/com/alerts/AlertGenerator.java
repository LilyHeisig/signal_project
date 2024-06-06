package com.alerts;

import com.alerts.alert_strategies.BloodPressureAlertStrategy;
import com.alerts.alert_strategies.ECGDataAlertStrategy;
import com.alerts.alert_strategies.HypotensiveHypoxemiaAlertStrategy;
import com.alerts.alert_strategies.OxygenSaturationAlertStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.staff_devices.SimpleStaffGUI;
import com.staff_devices.StaffDevice;
import com.alerts.alert_strategies.AlertStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private AlertPublisher alertPublisher;
    private List<AlertStrategy> alertStrategies;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator() {
        this.alertPublisher = AlertPublisher.getInstance();
        StaffDevice device = new SimpleStaffGUI(); //ngl this could be better designed to allow for different devices
        alertPublisher.subscribe(device);
        alertStrategies = new ArrayList<AlertStrategy>();
        alertStrategies.add(new BloodPressureAlertStrategy());
        alertStrategies.add(new OxygenSaturationAlertStrategy());
        alertStrategies.add(new HypotensiveHypoxemiaAlertStrategy());
        alertStrategies.add(new ECGDataAlertStrategy());
    }

    /**
     * Another constructor that lets you specify the alert strategies to use.
     * @param alertStrategies
     */
    public AlertGenerator(List<AlertStrategy> alertStrategies) {
        this.alertPublisher = AlertPublisher.getInstance();
        StaffDevice device = new SimpleStaffGUI(); //ngl this could be better designed to allow for different devices
        alertPublisher.subscribe(device);
        this.alertStrategies = alertStrategies;
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
        // check if the patient has any alerts using the alert strategies and trigger an alert if necessary
        for (AlertStrategy alertStrategy : alertStrategies) {
            Alert alert = alertStrategy.checkAlert(patientRecords); //checkAlerts returns a relevant Alert
            if (alert != null) {
                triggerAlert(alert);
            }
        }
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
