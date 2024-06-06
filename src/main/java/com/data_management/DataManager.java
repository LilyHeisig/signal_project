package com.data_management;

import java.net.URI;
import java.util.List;

import com.alerts.AlertGenerator;
import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataReader;
import com.data_management.SimpleWebSocketClient;

/**
 * This class is responsible for managing the data within the healthcare monitoring system.
 * It receives the data from the DataReader, stores it in the DataStorage and sends it to the AlertGenerator.
 */
public class DataManager {
    private DataStorage dataStorage;
    private static DataManager instance;
    private AlertGenerator alertGenerator;

    /**
     * Main method of the application. Initialises the data generation system.
     */
    public static void main(String[] args) {
        // Create an instance of the DataManager
        DataManager dataManager = DataManager.getInstance();
        // Set the arguments for the data generation system
        args = "--patient-count 100 --output websocket:8080".split(" ");
        // start the data generation system
        try {
            HealthDataSimulator.main(args);
        } catch (Exception e) {
            System.err.println("DataManager found: An error occurred while starting the data generation system. " + e.getMessage());
        }
        // start the data management system
        try {
            URI uri = new URI("ws://localhost:8080");
            DataReader reader = new SimpleWebSocketClient(uri);
            reader.readData(dataManager.getDataStorage());
        } catch (Exception e) {
            System.err.println("DataManager found: An error occurred while starting the data management system. " + e.getMessage());
        }
    }

    /**
     * I use a singleton design pattern to ensure that only one instance of the DataManager is created:
     * The Constructor is private and the getInstance method is static.
     */
    private DataManager() { 
        this.dataStorage = DataStorage.getInstance();
        this.alertGenerator = new AlertGenerator();
    }
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    /**
     * This method is responsible for managing the data received from the DataReader.
     * @param patientId
     * @param measurementValue
     * @param recordType
     * @param timestamp
     */
    public void manageData(int patientId, double measurementValue, String recordType, long timestamp) {
        // Add the data to the data storage
        try {
            dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
        } catch (IllegalArgumentException e) {
            System.err.println("DataManager found: An error occurred while adding the data to the data storage. " + e.getMessage());
        } catch (Exception e) {
            System.err.println("DataManager found: Unknown Error when calling addPatientData in dataStorage. " + e.getMessage());
        }
        // Send the data to the AlertGenerator
        System.out.println("DataManager: Sending data to the AlertGenerator...");
        try {
            alertGenerator.evaluateData(dataStorage.getPatient(patientId), getRecords(patientId));
            System.out.println("DataManager: Data sent to the AlertGenerator.");
        } catch (Exception e) {
            System.err.println("DataManager found: An error occurred while evaluating the patient data. " + e.getMessage());
        }
    }

    private List<PatientRecord> getRecords(int patientId) {
        // find the relevant start and end time of the data
        long currentTime = System.currentTimeMillis();
        // set the start time at 6 hours ago
        long startTime = currentTime - 6 * 60 * 60 * 1000;
        System.out.println("fetching patient records");
        return dataStorage.getRecords(patientId, startTime, currentTime);
    }
    
    /**
     * Getter for the data storage.
     */
    public DataStorage getDataStorage() {
        return dataStorage;
    }
}
