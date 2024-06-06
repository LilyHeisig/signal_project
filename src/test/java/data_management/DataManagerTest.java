package data_management;

import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import com.alerts.AlertGenerator;
import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataManager;
import com.data_management.DataReader;
import com.data_management.SimpleWebSocketClient;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

class DataManagerTest {

    @Mock
    private DataStorage dataStorage;

    @Mock
    private AlertGenerator alertGenerator;

    private DataManager dataManager;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        dataManager = DataManager.getInstance();
        dataManager = spy(dataManager); // Spy the instance to replace the dataStorage and alertGenerator with mocks

        // Use reflection to set the private field alertGenerator
        Field alertGeneratorField = DataManager.class.getDeclaredField("alertGenerator");
        alertGeneratorField.setAccessible(true);
        alertGeneratorField.set(dataManager, alertGenerator);

        doReturn(dataStorage).when(dataManager).getDataStorage();
    }

    @Test
    void testSingletonGetInstance() {
        DataManager instance1 = DataManager.getInstance();
        DataManager instance2 = DataManager.getInstance();
        assertSame(instance1, instance2, "DataManager should follow the singleton pattern and return the same instance");
    }

    @Test
    void testManageData() throws Exception {
        int patientId = 1;
        double measurementValue = 98.6;
        String recordType = "Saturation";
        long timestamp = System.currentTimeMillis();

        Patient patient = mock(Patient.class);
        List<PatientRecord> records = Arrays.asList(mock(PatientRecord.class), mock(PatientRecord.class));

        when(dataStorage.getPatient(patientId)).thenReturn(patient);
        when(dataStorage.getRecords(eq(patientId), anyLong(), anyLong())).thenReturn(records);

        dataManager.manageData(patientId, measurementValue, recordType, timestamp);

        verify(dataStorage).addPatientData(patientId, measurementValue, recordType, timestamp);
        verify(alertGenerator).evaluateData(patient, records);
    }

    @Test
    void testManageDataWithExceptionInAddPatientData() throws Exception{
        int patientId = 1;
        double measurementValue = 98.6;
        String recordType = "Saturation";
        long timestamp = System.currentTimeMillis();

        doThrow(new IllegalArgumentException("Invalid data")).when(dataStorage).addPatientData(patientId, measurementValue, recordType, timestamp);

        assertDoesNotThrow(() -> dataManager.manageData(patientId, measurementValue, recordType, timestamp));
        verify(dataStorage).addPatientData(patientId, measurementValue, recordType, timestamp);
        verify(alertGenerator, never()).evaluateData(any(), any());
    }

    @Test
    void testManageDataWithExceptionInEvaluateData() throws Exception{
        int patientId = 1;
        double measurementValue = 98.6;
        String recordType = "Saturation";
        long timestamp = System.currentTimeMillis();

        Patient patient = mock(Patient.class);
        List<PatientRecord> records = Arrays.asList(mock(PatientRecord.class), mock(PatientRecord.class));

        when(dataStorage.getPatient(patientId)).thenReturn(patient);
        when(dataStorage.getRecords(eq(patientId), anyLong(), anyLong())).thenReturn(records);
        doThrow(new RuntimeException("Evaluation error")).when(alertGenerator).evaluateData(patient, records);

        assertDoesNotThrow(() -> dataManager.manageData(patientId, measurementValue, recordType, timestamp));
        verify(dataStorage).addPatientData(patientId, measurementValue, recordType, timestamp);
        verify(alertGenerator).evaluateData(patient, records);
    }
}
