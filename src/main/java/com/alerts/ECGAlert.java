package com.alerts;

public class ECGAlert implements Alert{
    
    private int patientId;
    private long timestamp;
    private String condition = "ECGAlert";
    private String message = "ECGAlert triggered for patien number " + patientId + ", at time " + timestamp;
    
    public ECGAlert(int patientId, long timestamp) {
        this.patientId = patientId;
        this.timestamp = timestamp;
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
