package alerts.alert_strategies;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;
import com.alerts.alert_strategies.OxygenSaturationAlertStrategy;
import com.alerts.BloodOxygenAlertFactory;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OxygenSaturationAlertStrategyTest {

    private OxygenSaturationAlertStrategy alertStrategy;
    private BloodOxygenAlertFactory mockAlertFactory;

    @BeforeEach
    public void setUp() {
        mockAlertFactory = mock(BloodOxygenAlertFactory.class);
        alertStrategy = new OxygenSaturationAlertStrategy();
    }

    @Test
    public void testCheckAlert_LowSaturationDetected() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(new PatientRecord(1, 91, "Saturation", 1622505600000L));

        Alert mockAlert = new BloodOxygenAlert(1, 1622505600000L);
        when(mockAlertFactory.createAlert(1, 1622505600000L)).thenReturn(mockAlert);

        Alert alert = alertStrategy.checkAlert(patientRecords);

        assertNotNull(alert);
        assertEquals("Blood Oxygen Alert triggered for patient number 1, at time 1622505600000", alert.getMessage());
    }

    @Test
    public void testCheckAlert_RapidDropDetected() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(new PatientRecord(1, 98, "Saturation", 1622505000000L));
        patientRecords.add(new PatientRecord(1, 92, "Saturation", 1622505600000L));

        Alert mockAlert = new BloodOxygenAlert(1, 1622505600000L);
        when(mockAlertFactory.createAlert(1, 1622505600000L)).thenReturn(mockAlert);

        Alert alert = alertStrategy.checkAlert(patientRecords);

        assertNotNull(alert);
        assertEquals("Blood Oxygen Alert triggered for patient number 1, at time 1622505600000", alert.getMessage());
    }

    @Test
    public void testCheckAlert_NoCriticalSaturation() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(new PatientRecord(1, 94, "Saturation", 1622505600000L));

        Alert alert = alertStrategy.checkAlert(patientRecords);

        assertNull(alert);
    }

    @Test
    public void testCheckAlert_EmptyPatientRecords() {
        List<PatientRecord> patientRecords = new ArrayList<>();

        Alert alert = alertStrategy.checkAlert(patientRecords);

        assertNull(alert);
    }

    @Test
    public void testFilterRecords() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(new PatientRecord(1, 94, "Saturation",  1622505600000L));
        patientRecords.add(new PatientRecord(1, 80, "HeartRate", 1622505600000L));

        List<PatientRecord> filteredRecords = alertStrategy.filterRecords(patientRecords);

        assertEquals(1, filteredRecords.size());
        assertEquals("Saturation", filteredRecords.get(0).getRecordType());
    }

    // Additional tests can be added here for other scenarios
}
