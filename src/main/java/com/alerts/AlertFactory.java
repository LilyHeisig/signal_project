package com.alerts;
/**
 * The {@code AlertFactory} interface is used to create alerts.
 */
public interface AlertFactory {
    /**
     * This interface is used to create alerts.
     * 
     * @param patientId
     * @param condition
     * @param timestamp
     * @return Alert
     */
    public Alert createAlert(int patientId, long timestamp);
}
