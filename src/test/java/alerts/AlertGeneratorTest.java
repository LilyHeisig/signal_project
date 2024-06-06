package alerts;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.AlertPublisher;
import com.alerts.alert_strategies.*;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.staff_devices.SimpleStaffGUI;
import com.staff_devices.StaffDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlertGeneratorTest {

    private AlertGenerator alertGenerator;
    private AlertPublisher alertPublisherMock;
    private AlertStrategy bloodPressureAlertStrategyMock;
    private AlertStrategy oxygenSaturationAlertStrategyMock;
    private AlertStrategy hypotensiveHypoxemiaAlertStrategyMock;
    private AlertStrategy ecgDataAlertStrategyMock;

    @BeforeEach
    public void setUp() {
        alertPublisherMock = mock(AlertPublisher.class);
        bloodPressureAlertStrategyMock = mock(BloodPressureAlertStrategy.class);
        oxygenSaturationAlertStrategyMock = mock(OxygenSaturationAlertStrategy.class);
        hypotensiveHypoxemiaAlertStrategyMock = mock(HypotensiveHypoxemiaAlertStrategy.class);
        ecgDataAlertStrategyMock = mock(ECGDataAlertStrategy.class);

        List<AlertStrategy> alertStrategies = Arrays.asList(
                bloodPressureAlertStrategyMock,
                oxygenSaturationAlertStrategyMock,
                hypotensiveHypoxemiaAlertStrategyMock,
                ecgDataAlertStrategyMock
        );

        alertGenerator = new AlertGenerator(alertStrategies);
        // Use reflection to inject the mock AlertPublisher
        try {
            var field = AlertGenerator.class.getDeclaredField("alertPublisher");
            field.setAccessible(true);
            field.set(alertGenerator, alertPublisherMock);
        } catch (Exception e) {
            fail("Failed to inject mock AlertPublisher", e);
        }

        // Mock subscription behavior
        StaffDevice device = new SimpleStaffGUI();
        doNothing().when(alertPublisherMock).subscribe(device);
    }

    @Test
    public void testEvaluateData_noPatientRecords() throws Exception {
        Patient patient = mock(Patient.class);
        List<PatientRecord> patientRecords = new ArrayList<>();

        alertGenerator.evaluateData(patient, patientRecords);

        verifyNoInteractions(bloodPressureAlertStrategyMock);
        verifyNoInteractions(oxygenSaturationAlertStrategyMock);
        verifyNoInteractions(hypotensiveHypoxemiaAlertStrategyMock);
        verifyNoInteractions(ecgDataAlertStrategyMock);
    }

    @Test
    public void testEvaluateData_triggerAlert() throws Exception {
        Patient patient = mock(Patient.class);
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(mock(PatientRecord.class));

        Alert alert = mock(Alert.class);
        when(bloodPressureAlertStrategyMock.checkAlert(patientRecords)).thenReturn(alert);

        alertGenerator.evaluateData(patient, patientRecords);

        verify(bloodPressureAlertStrategyMock).checkAlert(patientRecords);
        verify(alertPublisherMock).publishAlert(alert);
    }

    @Test
    public void testEvaluateData_noAlertsTriggered() throws Exception {
        Patient patient = mock(Patient.class);
        List<PatientRecord> patientRecords = new ArrayList<>();
        patientRecords.add(mock(PatientRecord.class));

        when(bloodPressureAlertStrategyMock.checkAlert(patientRecords)).thenReturn(null);
        when(oxygenSaturationAlertStrategyMock.checkAlert(patientRecords)).thenReturn(null);
        when(hypotensiveHypoxemiaAlertStrategyMock.checkAlert(patientRecords)).thenReturn(null);
        when(ecgDataAlertStrategyMock.checkAlert(patientRecords)).thenReturn(null);

        alertGenerator.evaluateData(patient, patientRecords);

        verify(bloodPressureAlertStrategyMock).checkAlert(patientRecords);
        verify(oxygenSaturationAlertStrategyMock).checkAlert(patientRecords);
        verify(hypotensiveHypoxemiaAlertStrategyMock).checkAlert(patientRecords);
        verify(ecgDataAlertStrategyMock).checkAlert(patientRecords);

        verify(alertPublisherMock, never()).publishAlert(any(Alert.class));
    }

    @Test
    public void testTriggerAlert() {
        Alert alert = mock(Alert.class);
        when(alert.getPatientId()).thenReturn(12345);
        when(alert.getTimestamp()).thenReturn(1672576800000L);
        when(alert.getCondition()).thenReturn("Critical Condition");

        // Using reflection to access and invoke the private triggerAlert method
        try {
            var method = AlertGenerator.class.getDeclaredMethod("triggerAlert", Alert.class);
            method.setAccessible(true);
            method.invoke(alertGenerator, alert);
        } catch (Exception e) {
            fail("Failed to invoke triggerAlert method", e);
        }

        verify(alertPublisherMock).publishAlert(alert);
    }
}
