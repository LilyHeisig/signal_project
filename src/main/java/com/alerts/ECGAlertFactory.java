package com.alerts;

public class ECGAlertFactory implements AlertFactory{
    @Override
    public Alert createAlert(int patientId, long timestamp) {
        return new ECGAlert(patientId, timestamp);
    }
}
