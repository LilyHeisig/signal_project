package alerts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;

import com.alerts.AlertGenerator;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.DataStorage;

import java.lang.reflect.Method;


public class AlertGeneratorTest {

    /**
     * This test should check if the ECGData method in the alert generator
     * used to determine alerts from incoming patient data are working correctly.
     * It simulates a scenario where the ECGData is critical.
     * 
     * param: none
     */
    @Test
    void testIsECGDataCritical() throws Exception {
        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        int patientId = 1;

        ArrayList<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(patientId, 100.0, "ECGData", 1714376789050L));
        records.add(new PatientRecord(patientId, 200.0, "ECGData", 1714376789051L));
        records.add(new PatientRecord(patientId, 100.0, "ECGData", 1714376789052L));
        records.add(new PatientRecord(patientId, 200.0, "ECGData", 1714376789053L));
        records.add(new PatientRecord(patientId, 100.0, "ECGData", 1714376789054L));
        records.add(new PatientRecord(patientId, 600.0, "ECGData", 1714376789055L));

        // Check if the method returns true on this input
        Method method = AlertGenerator.class.getDeclaredMethod("isECGDataCritical", List.class);
        method.setAccessible(true);
        Object result = method.invoke(alertGenerator, records);

        assertEquals(true, result);
    }

    /**
     * This test should check if the blood saturation method in the alert generator
     * used to determine alerts from incoming patient data are working correctly.
     * This test simulates a scenario where the blood saturation is critically low.
     * 
     * param: none
     */
    @Test
    void testIsBloodSaturationCritical() throws Exception {
        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        int patientId = 1;

        ArrayList<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(patientId, 100.0, "bloodSaturation", 1714376789050L));
        records.add(new PatientRecord(patientId, 95.0, "bloodSaturation", 1714376789051L));
        records.add(new PatientRecord(patientId, 85.0, "bloodSaturation", 1714376789053L));

        // Check if the method returns true on this input
        Method method = AlertGenerator.class.getDeclaredMethod("isBloodSaturationCritical", List.class);
        method.setAccessible(true);
        Object result = method.invoke(alertGenerator, records);

        assertEquals(true, result);
    }

    /**
     * This test should check if the blood pressure method in the alert generator
     * used to determine alerts from incoming patient data are working correctly.
     * This test simulates a scenario where the blood pressure is critical according
     * to the trend alert.
     * 
     * param: none
     */
    @Test
    void testIsBloodPressureCritical() throws Exception {
        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        int patientId = 1;


        ArrayList<PatientRecord> systolicPressure = new ArrayList<>();
        systolicPressure.add(new PatientRecord(patientId, 100.0, "systolicPressure", 1714376789050L));
        systolicPressure.add(new PatientRecord(patientId, 110.0, "systolicPressure", 1714376789051L));
        systolicPressure.add(new PatientRecord(patientId, 120.0, "systolicPressure", 1714376789052L));

        ArrayList<PatientRecord> diastolicPressure = new ArrayList<>();
        diastolicPressure.add(new PatientRecord(patientId, 60.0, "diastolicPressure", 1714376789050L));
        diastolicPressure.add(new PatientRecord(patientId, 71.0, "diastolicPressure", 1714376789051L));
        diastolicPressure.add(new PatientRecord(patientId, 82.0, "diastolicPressure", 1714376789052L));

        // Check if the method returns true on this input
        Method method = AlertGenerator.class.getDeclaredMethod("isBloodPressureCritical", List.class, List.class);
        method.setAccessible(true);
        Object result = method.invoke(alertGenerator, systolicPressure, diastolicPressure);

        assertEquals(true, result);
    }

    /**
     * This test should check if the blood pressure method in the alert generator
     * used to determine alerts from incoming patient data are working correctly.
     * This test simulates a scenario where the blood pressure is critical according
     * to the critical threshold alert.
     * 
     * param: none
     */
    @Test
    void testIsBloodPressureCriticalThreshold() throws Exception {
        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        int patientId = 1;

        ArrayList<PatientRecord> systolicPressure = new ArrayList<>();
        systolicPressure.add(new PatientRecord(patientId, 100.0, "systolicPressure", 1714376789050L));
        systolicPressure.add(new PatientRecord(patientId, 110.0, "systolicPressure", 1714376789051L));
        systolicPressure.add(new PatientRecord(patientId, 60, "systolicPressure", 1714376789052L));

        ArrayList<PatientRecord> diastolicPressure = new ArrayList<>();
        diastolicPressure.add(new PatientRecord(patientId, 60.0, "diastolicPressure", 1714376789050L));
        diastolicPressure.add(new PatientRecord(patientId, 70.0, "diastolicPressure", 1714376789051L));
        diastolicPressure.add(new PatientRecord(patientId, 70.0, "diastolicPressure", 1714376789052L));

        // Check if the method returns true on this input
        Method method = AlertGenerator.class.getDeclaredMethod("isBloodPressureCritical", List.class, List.class);
        method.setAccessible(true);
        Object result = method.invoke(alertGenerator, systolicPressure, diastolicPressure);

        assertEquals(true, result);
    }

    /**
     * This test checks if the isThereHypotensiveHypoxemia method in the alert generator
     * used to determine alerts from incoming patient data are working correctly.
     * This test simulates a scenario where the patient has hypotensive hypoxemia.
     * 
     * param: none
     */
    @Test
    void testIsThereHypotensiveHypoxemia() throws Exception {
        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        int patientId = 1;

        ArrayList<PatientRecord> bloodSaturation = new ArrayList<>();
        bloodSaturation.add(new PatientRecord(patientId, 100.0, "bloodSaturation", 1714376789050L));
        bloodSaturation.add(new PatientRecord(patientId, 95.0, "bloodSaturation", 1714376789051L));
        bloodSaturation.add(new PatientRecord(patientId, 85.0, "bloodSaturation", 1714376789052L));

        ArrayList<PatientRecord> systolicPressure = new ArrayList<>();
        systolicPressure.add(new PatientRecord(patientId, 100.0, "systolicPressure", 1714376789050L));
        systolicPressure.add(new PatientRecord(patientId, 110.0, "systolicPressure", 1714376789051L));
        systolicPressure.add(new PatientRecord(patientId, 80.0, "systolicPressure", 1714376789052L));

        // Check if the method returns true on this input
        Method method = AlertGenerator.class.getDeclaredMethod("isThereHypotensiveHypoxemia", List.class, List.class);
        method.setAccessible(true);
        Object result = method.invoke(alertGenerator, bloodSaturation, systolicPressure);
        
        assertEquals(true, result);
    }
    /**
     * A test for the alert generator. This test should check if the alert generator
     * is able to generate alerts for patients based on their records.
     * 
     * param: 
     */
    @Test
    void testECGAlertGenerator() {
        DataStorage storage = new DataStorage();
        int patientId = 1;
        Patient patient = new Patient(patientId);
        long time = System.currentTimeMillis();

        storage.addPatientData(patientId, 100.0, "ECGData", (time-1000L));
        storage.addPatientData(patientId, 200.0, "ECGData", (time-900L));
        storage.addPatientData(patientId, 100.0, "ECGData", (time-800L));
        storage.addPatientData(patientId, 200.0, "ECGData", (time-700L));
        storage.addPatientData(patientId, 100.0, "ECGData", (time-600L));
        storage.addPatientData(patientId, 600.0, "ECGData", (time-500L));

        // check if storage has the patient record saved correctly
        List<PatientRecord> records = storage.getRecords(patientId, (time-10000L), time);
        assertEquals(6, records.size());

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        // Check if the alert is generated
        // Create a stream to hold the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            alertGenerator.evaluateData(patient);
        } catch (Exception e) {
            System.err.println("An error occurred while evaluating patient data: " + e.getMessage());
        }

        // Restore the original System.out
        System.setOut(originalOut);

        // Assert that System.out.println() was called
        assertFalse(outContent.toString().isEmpty());
    }
}
