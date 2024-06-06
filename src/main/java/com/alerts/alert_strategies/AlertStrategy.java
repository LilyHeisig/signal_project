package com.alerts.alert_strategies;

import java.util.List;
import com.data_management.PatientRecord;
import com.alerts.Alert;

public interface AlertStrategy {
    public Alert checkAlert(List<PatientRecord> patientRecords);
}