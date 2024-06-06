package com.alerts;
/**
 * The {@code RepeatedAlertDecorator} class is used to create repeated alerts.
 */
public class RepeatedAlertDecorator extends AlertDecorator{
    private int repeatCount;

    public RepeatedAlertDecorator(Alert alert, int repeatCount){
        super(alert);
        this.repeatCount = repeatCount;
    }

    @Override
    public String getMessage() {
        return " Repeated " + repeatCount + " times: " + alert.getMessage();
    }
}
