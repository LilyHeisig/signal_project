package com.alerts;
/**
 * The {@code BloodOxygenAlertFactory} class is used to create blood oxygen alerts.
 */
public class BloodOxygenAlertFactory implements AlertFactory{
    @Override
    public Alert createAlert(int patientId, long timestamp) {
        return new BloodOxygenAlert(patientId, timestamp);
    }
}