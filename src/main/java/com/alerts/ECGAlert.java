package com.alerts;
/**
 * The {@code ECGAlert} class represents an alert that is triggered when an ECG alert is detected.
 */
public class ECGAlert implements Alert{
    
    private int patientId;
    private long timestamp;
    private String condition = "ECGAlert";
    private String message;
    
    public ECGAlert(int patientId, long timestamp) {
        this.patientId = patientId;
        this.timestamp = timestamp;
        message = "ECGAlert triggered for patient number " + patientId + ", at time " + timestamp;
    }
    
    @Override
    public String getCondition() {
        return condition;
    }
    
    @Override
    public int getPatientId() {
        return patientId;
    }
    
    @Override
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}
