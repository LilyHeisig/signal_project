package com.alerts;
/**
 * The {@code PriorityAlertDecorator} class is used to decorate alerts with a priority.
 */
public class PriorityAlertDecorator extends AlertDecorator{
    private int priority;

    public PriorityAlertDecorator(Alert alert, int priority){
        super(alert);
        this.priority = priority;
    }

    @Override
    public String getMessage() {
        return " Priority: " + priority + " " + alert.getMessage();
    }
}
