package com.alerts;

public interface Alert {
    public String getCondition();
    public int getPatientId();
    public long getTimestamp();
    public String getMessage();
}