package data_management;

import org.junit.jupiter.api.Test;

import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class PatientGetRecordsTest {
    /*
     * A test for the getRecords method in the Patient class.
     */
    @Test
    void testGetRecords() {
        // Create a new Patient object
        Patient patient = new Patient(1);

        // Add some records to the patient
        patient.addRecord(100.0, "WhiteBloodCells", 1714376789050L);
        patient.addRecord(200.0, "WhiteBloodCells", 1714376789051L);

        // Get the records for the patient
        List<PatientRecord> records = patient.getRecords(1714376789050L, 1714376789051L);

        // Check if two records are retrieved
        assertEquals(2, records.size());

        // Validate the first record
        assertEquals(100.0, records.get(0).getMeasurementValue());
    }
}
