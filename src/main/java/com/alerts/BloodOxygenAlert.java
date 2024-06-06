package com.alerts;

public class BloodOxygenAlert implements Alert{
    private int patientId;
    private long timestamp;
    private String condition = "Blood Oxygen";
    private String message = "Blood Oxygen Alert triggered for patient number " + patientId + ", at time " + timestamp;

    public BloodOxygenAlert(int patientId, long timestamp){
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
