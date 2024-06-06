package alerts.alert_strategies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.alerts.Alert;
import com.alerts.alert_strategies.ECGDataAlertStrategy;
import com.alerts.ECGAlertFactory;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ECGDataAlertStrategyTest {

    private ECGDataAlertStrategy ecgDataAlertStrategy;
    private ECGAlertFactory alertFactoryMock;

    @BeforeEach
    public void setUp() {
        alertFactoryMock = mock(ECGAlertFactory.class);
        ecgDataAlertStrategy = new ECGDataAlertStrategy();
        
        // Use reflection to inject the mock AlertFactory
        try {
            var field = ECGDataAlertStrategy.class.getDeclaredField("alertFactory");
            field.setAccessible(true);
            field.set(ecgDataAlertStrategy, alertFactoryMock);
        } catch (Exception e) {
            fail("Failed to inject mock AlertFactory", e);
        }
    }

    @Test
    public void testCheckAlert_NoCriticalCondition() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(createPatientRecord("ECGData", 1.0));
        patientRecords.add(createPatientRecord("ECGData", 1.1));
        patientRecords.add(createPatientRecord("ECGData", 1.2));

        Alert alert = ecgDataAlertStrategy.checkAlert(patientRecords);

        assertNull(alert, "No alert should be generated for normal ECG data readings");
    }

    @Test
    public void testCheckAlert_CriticalCondition() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(createPatientRecord("ECGData", 1.0));
        patientRecords.add(createPatientRecord("ECGData", 1.1));
        patientRecords.add(createPatientRecord("ECGData", 10.0)); // Critical spike

        Alert alertMock = mock(Alert.class);
        when(alertFactoryMock.createAlert(anyInt(), anyLong())).thenReturn(alertMock);

        Alert alert = ecgDataAlertStrategy.checkAlert(patientRecords);

        assertNotNull(alert, "An alert should be generated for critical ECG data readings");
        verify(alertFactoryMock).createAlert(anyInt(), anyLong());
        assertEquals("EC Alert triggered for patient number 1, at time 0", alert.getMessage());
    }

    @Test
    public void testCheckAlert_FilterRecords() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(createPatientRecord("ECGData", 1.0));
        patientRecords.add(createPatientRecord("HeartRate", 70));
        patientRecords.add(createPatientRecord("ECGData", 1.2));

        // Although filterRecords is private, we test its effect through checkAlert
        Alert alert = ecgDataAlertStrategy.checkAlert(patientRecords);
        
        // There should be no alert since no critical condition is met
        assertNull(alert, "No alert should be generated for the provided data");
    }

    @Test
    public void testCheckAlert_NoPatientRecords() {
        List<PatientRecord> patientRecords = new ArrayList<>();

        Alert alert = ecgDataAlertStrategy.checkAlert(patientRecords);

        assertNull(alert, "No alert should be generated when there are no patient records");
    }

    @Test
    public void testCheckAlert_CriticalConditionWithLowValues() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(createPatientRecord("ECGData", 1.0));
        patientRecords.add(createPatientRecord("ECGData", 1.1));
        patientRecords.add(createPatientRecord("ECGData", -10.0)); // Critical low spike

        Alert alertMock = mock(Alert.class);
        when(alertFactoryMock.createAlert(anyInt(), anyLong())).thenReturn(alertMock);

        Alert alert = ecgDataAlertStrategy.checkAlert(patientRecords);

        assertNotNull(alert, "An alert should be generated for critical low ECG data readings");
        verify(alertFactoryMock).createAlert(anyInt(), anyLong());
    }

    private PatientRecord createPatientRecord(String recordType, double measurementValue) {
        PatientRecord record = mock(PatientRecord.class);
        when(record.getRecordType()).thenReturn(recordType);
        when(record.getMeasurementValue()).thenReturn(measurementValue);
        when(record.getPatientId()).thenReturn(1);
        when(record.getTimestamp()).thenReturn(System.currentTimeMillis());
        return record;
    }
}
