package com.alerts;
/**
 * The {@code Alert} interface represents an alert that can be displayed to staff members.
 */

public interface Alert {
    public String getCondition();
    public int getPatientId();
    public long getTimestamp();
    public String getMessage();
}