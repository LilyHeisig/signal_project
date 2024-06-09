package com.alerts;
/**
 * The {@code BloodOxygenAlert} class represents an alert that can be displayed to staff members.
 */
public class BloodOxygenAlert implements Alert{
    private int patientId;
    private long timestamp;
    private String condition = "Blood Oxygen";
    private String message;

    public BloodOxygenAlert(int patientId, long timestamp){
        this.patientId = patientId;
        this.timestamp = timestamp;
        message = "Blood Oxygen Alert triggered for patient number " + patientId + ", at time " + timestamp;
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
