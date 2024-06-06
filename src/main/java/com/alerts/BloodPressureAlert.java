package com.alerts;

public class BloodPressureAlert implements Alert{
    private int patientId;
    private long timestamp;
    private String condition = "Blood Pressure";
    private String message = "Blood Pressure Alert triggered for patient number " + patientId + ", at time " + timestamp;

    public BloodPressureAlert(int patientId, long timestamp){
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
