package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.Patient;

import java.util.List;

import static org.mockito.Mockito.*;

import com.alerts.AlertGenerator;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Map;

public class DataStorageTest {

    private DataStorage dataStorage;

    @BeforeEach
    public void setUp() {
        dataStorage = DataStorage.getInstance();
        // Use reflection to reset the singleton instance for isolated tests
        try {
            var field = DataStorage.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, null);
        } catch (Exception e) {
            fail("Failed to reset DataStorage singleton instance", e);
        }
        dataStorage = DataStorage.getInstance();
    }

    @Test
    public void testGetInstance() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "Both instances should be the same");
    }

    @Test
    void testAddAndGetRecords() {
        // TODO Perhaps you can implement a mock data reader to mock the test data?
        // DataReader reader

        //DataStorage storage = new DataStorage(reader);
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }

    @Test
    public void testAddPatientData_NewPatient() {
        int patientId = 1;
        double measurementValue = 98.6;
        String recordType = "Temperature";
        long timestamp = System.currentTimeMillis();

        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

        Patient patient = dataStorage.getPatient(patientId);
        assertNotNull(patient, "Patient should be created and added to the storage");

        List<PatientRecord> records = patient.getRecords(timestamp, timestamp + 1);
        assertEquals(1, records.size(), "There should be one record for the new patient");
        assertEquals(measurementValue, records.get(0).getMeasurementValue(), "Measurement value should match");
        assertEquals(recordType, records.get(0).getRecordType(), "Record type should match");
        assertEquals(timestamp, records.get(0).getTimestamp(), "Timestamp should match");
    }

    @Test
    public void testAddPatientData_ExistingPatient_NewRecord() {
        int patientId = 1;
        double measurementValue1 = 98.6;
        String recordType1 = "Temperature";
        long timestamp1 = System.currentTimeMillis();

        double measurementValue2 = 120.0;
        String recordType2 = "HeartRate";
        long timestamp2 = System.currentTimeMillis() + 1000;

        dataStorage.addPatientData(patientId, measurementValue1, recordType1, timestamp1);
        dataStorage.addPatientData(patientId, measurementValue2, recordType2, timestamp2);

        Patient patient = dataStorage.getPatient(patientId);
        assertNotNull(patient, "Patient should be created and added to the storage");

        List<PatientRecord> records = patient.getRecords(timestamp1, timestamp2 + 1);
        assertEquals(2, records.size(), "There should be two records for the patient");
    }

    @Test
    public void testAddPatientData_ExistingPatient_DuplicateRecord() {
        int patientId = 1;
        double measurementValue = 98.6;
        String recordType = "Temperature";
        long timestamp = System.currentTimeMillis();

        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
        });

        String expectedMessage = "Record already exists.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetRecords() {
        int patientId = 1;
        double measurementValue = 98.6;
        String recordType = "Temperature";
        long timestamp = System.currentTimeMillis();

        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

        List<PatientRecord> records = dataStorage.getRecords(patientId, timestamp - 1000, timestamp + 1000);
        assertEquals(1, records.size(), "There should be one record in the specified time range");
        assertEquals(measurementValue, records.get(0).getMeasurementValue(), "Measurement value should match");
        assertEquals(recordType, records.get(0).getRecordType(), "Record type should match");
        assertEquals(timestamp, records.get(0).getTimestamp(), "Timestamp should match");
    }

    @Test
    public void testGetAllPatients() {
        int patientId1 = 1;
        int patientId2 = 2;
        double measurementValue1 = 98.6;
        double measurementValue2 = 120.0;
        String recordType1 = "Temperature";
        String recordType2 = "HeartRate";
        long timestamp1 = System.currentTimeMillis();
        long timestamp2 = System.currentTimeMillis() + 1000;

        dataStorage.addPatientData(patientId1, measurementValue1, recordType1, timestamp1);
        dataStorage.addPatientData(patientId2, measurementValue2, recordType2, timestamp2);

        List<Patient> patients = dataStorage.getAllPatients();
        assertEquals(2, patients.size(), "There should be two patients in the storage");
    }

    @Test
    public void testGetPatient() {
        int patientId = 1;
        double measurementValue = 98.6;
        String recordType = "Temperature";
        long timestamp = System.currentTimeMillis();

        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

        Patient patient = dataStorage.getPatient(patientId);
        assertNotNull(patient, "Patient should be retrieved from the storage");
    }
}
