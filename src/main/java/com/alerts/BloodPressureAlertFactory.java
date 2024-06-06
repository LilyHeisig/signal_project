package com.alerts;
/**
 * The {@code BloodPressureAlertFactory} class is used to create blood pressure alerts.
 */
public class BloodPressureAlertFactory implements AlertFactory{
    @Override
    public Alert createAlert(int patientId, long timestamp) {
        return new BloodPressureAlert(patientId, timestamp);
    }
}
