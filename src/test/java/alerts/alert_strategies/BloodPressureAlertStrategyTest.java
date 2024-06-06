package alerts.alert_strategies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.alerts.Alert;
import com.alerts.alert_strategies.BloodPressureAlertStrategy;
import com.alerts.BloodPressureAlertFactory;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BloodPressureAlertStrategyTest {

    private BloodPressureAlertStrategy bloodPressureAlertStrategy;
    private BloodPressureAlertFactory alertFactoryMock;

    @BeforeEach
    public void setUp() {
        alertFactoryMock = mock(BloodPressureAlertFactory.class);
        bloodPressureAlertStrategy = new BloodPressureAlertStrategy();

        // Use reflection to inject the mock AlertFactory
        try {
            var field = BloodPressureAlertStrategy.class.getDeclaredField("AlertFactory");
            field.setAccessible(true);
            field.set(bloodPressureAlertStrategy, alertFactoryMock);
        } catch (Exception e) {
            fail("Failed to inject mock AlertFactory", e);
        }
    }

    @Test
    public void testCheckAlert_NoCriticalCondition() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(createPatientRecord("SystolicPressure", 120));
        patientRecords.add(createPatientRecord("DiastolicPressure", 80));

        Alert alert = bloodPressureAlertStrategy.checkAlert(patientRecords);

        assertNull(alert, "No alert should be generated for normal blood pressure readings");
    }

    @Test
    public void testCheckAlert_TrendAlert() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(createPatientRecord("SystolicPressure", 120));
        patientRecords.add(createPatientRecord("SystolicPressure", 135));
        patientRecords.add(createPatientRecord("SystolicPressure", 150));

        Alert alertMock = mock(Alert.class);
        when(alertFactoryMock.createAlert(anyInt(), anyLong())).thenReturn(alertMock);

        Alert alert = bloodPressureAlertStrategy.checkAlert(patientRecords);

        assertNotNull(alert, "An alert should be generated for a trend in blood pressure readings");
        verify(alertFactoryMock).createAlert(anyInt(), anyLong());
    }

    @Test
    public void testCheckAlert_CriticalThresholdAlert() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(createPatientRecord("SystolicPressure", 190));
        patientRecords.add(createPatientRecord("DiastolicPressure", 130));

        Alert alertMock = mock(Alert.class);
        when(alertFactoryMock.createAlert(anyInt(), anyLong())).thenReturn(alertMock);

        Alert alert = bloodPressureAlertStrategy.checkAlert(patientRecords);

        assertNotNull(alert, "An alert should be generated for critical blood pressure readings");
        verify(alertFactoryMock).createAlert(anyInt(), anyLong());
    }

    @Test
    public void testCheckAlert_NoPatientRecords() {
        List<PatientRecord> patientRecords = new ArrayList<>();

        Alert alert = bloodPressureAlertStrategy.checkAlert(patientRecords);

        assertNull(alert, "No alert should be generated when there are no patient records");
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