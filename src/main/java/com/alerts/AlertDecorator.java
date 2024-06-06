package com.alerts;

/**
 * The {@code AlertDecorator} class is an abstract class that serves as the base
 * class for all alert decorators. This class implements the {@link Alert} interface
 * and provides a reference to the {@link Alert} object that it decorates.
 */
public abstract class AlertDecorator implements Alert{
    protected Alert alert;

    public AlertDecorator(Alert alert){
        this.alert = alert;
    }

    @Override
    public String getMessage() {
        return alert.getMessage();
    }

    @Override
    public long getTimestamp() {
        return alert.getTimestamp();
    }

    @Override
    public int getPatientId() {
        return alert.getPatientId();
    }

    @Override
    public String getCondition() {
        return alert.getCondition();
    }
}
