package alerts.alert_strategies;

import com.alerts.Alert;
import com.alerts.HypotensiveHypoxemiaAlert;
import com.alerts.AlertFactory;
import com.alerts.alert_strategies.HypotensiveHypoxemiaAlertStrategy;
import com.alerts.HypotensiveHypoxemiaAlertFactory;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HypotensiveHypoxemiaAlertStrategyTest {

    private HypotensiveHypoxemiaAlertStrategy alertStrategy;
    private HypotensiveHypoxemiaAlertFactory mockAlertFactory;

    @BeforeEach
    public void setUp() {
        mockAlertFactory = mock(HypotensiveHypoxemiaAlertFactory.class);
        alertStrategy = new HypotensiveHypoxemiaAlertStrategy();
        // Use reflection or setter to inject mockAlertFactory if needed
        // Here we're assuming we can set it via a constructor or method
        // alertStrategy.setAlertFactory(mockAlertFactory);
    }

    @Test
    public void testCheckAlert_HypotensiveHypoxemiaDetected() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(new PatientRecord(1, 91, "Saturation", 1622505600000L));
        patientRecords.add(new PatientRecord(1, 85, "SystolicPressure", 1622505600000L));
        
        Alert mockAlert = new HypotensiveHypoxemiaAlert(1, 1622505600000L);
        when(mockAlertFactory.createAlert(1, 1622505600000L)).thenReturn(mockAlert);

        Alert alert = alertStrategy.checkAlert(patientRecords);

        assertNotNull(alert);
        assertEquals("Hypotensive Hypoxemia alert triggered for patient number 1, at time 1622505600000", alert.getMessage());
    }

    @Test
    public void testCheckAlert_NoHypotensiveHypoxemia() {
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(new PatientRecord(1, 93, "Saturation", 1622505600000L));
        patientRecords.add(new PatientRecord(1, 95, "SystolicPressure", 1622505600000L));

        Alert alert = alertStrategy.checkAlert(patientRecords);

        assertNull(alert);
    }

    @Test
    public void testCheckAlert_EmptyPatientRecords() {
        List<PatientRecord> patientRecords = new ArrayList<>();

        Alert alert = alertStrategy.checkAlert(patientRecords);

        assertNull(alert);
    }

    // Additional tests can be added here for other scenarios
}
