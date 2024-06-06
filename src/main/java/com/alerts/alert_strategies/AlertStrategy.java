package com.alerts.alert_strategies;

import java.util.List;
import com.data_management.PatientRecord;
import com.alerts.Alert;
/**
 * The {@code AlertStrategy} interface is used to define the strategy for checking alerts.
 */
public interface AlertStrategy {
    public Alert checkAlert(List<PatientRecord> patientRecords);
}