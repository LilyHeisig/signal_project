package data_management;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import com.alerts.AlertGenerator;
import com.data_management.DataManager;
import com.data_management.DataStorage;
import com.data_management.Patient;

public class DataManagerTest {

    private DataManager dataManager;
    private DataStorage mockDataStorage;
    private AlertGenerator mockAlertGenerator;
    
    @BeforeEach
    public void setUp() {
        // Ensure we start with a fresh instance each time
        DataManager.getInstance().resetInstance();
        dataManager = DataManager.getInstance();
        
        // Mock the dependencies
        mockDataStorage = mock(DataStorage.class);
        mockAlertGenerator = mock(AlertGenerator.class);
        
        // Inject mocks
        dataManager.setDataStorage(mockDataStorage);
        dataManager.setAlertGenerator(mockAlertGenerator);
    }

    @Test
    public void testSingletonPattern() {
        DataManager instance1 = DataManager.getInstance();
        DataManager instance2 = DataManager.getInstance();
        assertSame(instance1, instance2, "DataManager should be a singleton");
    }

    @Test
    public void testManageDataSuccessfully() throws Exception {
        int patientId = 1;
        double measurementValue = 98.6;
        String recordType = "temperature";
        long timestamp = System.currentTimeMillis();

        when(mockDataStorage.getPatient(patientId)).thenReturn(new Patient(patientId));
        when(mockDataStorage.getRecords(eq(patientId), anyLong(), anyLong())).thenReturn(List.of());

        dataManager.manageData(patientId, measurementValue, recordType, timestamp);

        verify(mockDataStorage).addPatientData(eq(patientId), eq(measurementValue), eq(recordType), eq(timestamp));
        verify(mockAlertGenerator).evaluateData(any(Patient.class), anyList());
    }

    @Test
    public void testManageDataHandlesIllegalArgumentException() throws Exception {
        int patientId = 1;
        double measurementValue = 98.6;
        String recordType = "temperature";
        long timestamp = System.currentTimeMillis();

        doThrow(new IllegalArgumentException("Invalid data")).when(mockDataStorage).addPatientData(patientId, measurementValue, recordType, timestamp);

        dataManager.manageData(patientId, measurementValue, recordType, timestamp);

        verify(mockDataStorage).addPatientData(patientId, measurementValue, recordType, timestamp);
        verify(mockAlertGenerator, never()).evaluateData(any(Patient.class), anyList());
    }

    @Test
    public void testManageDataHandlesGeneralException() throws Exception {
        int patientId = 1;
        double measurementValue = 98.6;
        String recordType = "temperature";
        long timestamp = System.currentTimeMillis();

        doThrow(new RuntimeException("Unknown error")).when(mockDataStorage).addPatientData(patientId, measurementValue, recordType, timestamp);

        dataManager.manageData(patientId, measurementValue, recordType, timestamp);

        verify(mockDataStorage).addPatientData(patientId, measurementValue, recordType, timestamp);
        verify(mockAlertGenerator, never()).evaluateData(any(Patient.class), anyList());
    }
}
