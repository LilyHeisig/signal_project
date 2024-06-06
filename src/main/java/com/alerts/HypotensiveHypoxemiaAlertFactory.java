package com.alerts;
/**
 * The {@code HypotensiveHypoxemiaAlertFactory} class is used to create alerts.
 */
public class HypotensiveHypoxemiaAlertFactory implements AlertFactory{
    @Override
    public Alert createAlert(int patientId, long timestamp) {
        return new HypotensiveHypoxemiaAlert(patientId, timestamp);
    }
}
