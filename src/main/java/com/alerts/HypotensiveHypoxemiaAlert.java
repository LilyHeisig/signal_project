package com.alerts;
/**
 * The {@code HypotensiveHypoxemiaAlert} class represents an alert that can be displayed to staff members.
 */
public class HypotensiveHypoxemiaAlert implements Alert{
    private int patientId;
    private long timestamp;
    private String condition = "Hypotensive Hypoxemia";
    private String message;

    public HypotensiveHypoxemiaAlert(int patientId, long timestamp){
        this.patientId = patientId;
        this.timestamp = timestamp;
        message = "Hypotensive Hypoxemia alert triggered for patient number " + patientId + ", at time " + timestamp;
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
