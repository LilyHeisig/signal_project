package com.alerts;

public class HypotensiveHypoxemiaAlertFactory implements AlertFactory{
    @Override
    public Alert createAlert(int patientId, long timestamp) {
        return new HypotensiveHypoxemiaAlert(patientId, timestamp);
    }
}
