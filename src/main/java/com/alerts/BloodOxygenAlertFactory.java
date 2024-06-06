package com.alerts;

public class BloodOxygenAlertFactory implements AlertFactory{
    @Override
    public Alert createAlert(int patientId, long timestamp) {
        return new BloodOxygenAlert(patientId, timestamp);
    }
}