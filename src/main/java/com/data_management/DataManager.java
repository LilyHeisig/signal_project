package com.data_management;

import com.alerts.AlertGenerator;

/**
 * This class is responsible for managing the data within the healthcare monitoring system.
 * It receives the data from the DataReader, stores it in the DataStorage and sends it to the AlertGenerator.
 */
public class DataManager {
    private DataStorage dataStorage;
    private static DataManager instance;
    private AlertGenerator alertGenerator;

    /**
     * I use a singleton design pattern to ensure that only one instance of the DataManager is created:
     * The Constructor is private and the getInstance method is static.
     */
    private DataManager() { 
        this.dataStorage = new DataStorage();
        this.alertGenerator = new AlertGenerator(dataStorage);
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
            alertGenerator.evaluateData(dataStorage.getPatient(patientId));
            System.out.println("DataManager: Data sent to the AlertGenerator.");
        } catch (Exception e) {
            System.err.println("DataManager found: An error occurred while evaluating the patient data. " + e.getMessage());
        }
    }
    
    /**
     * Getter for the data storage. Mainly for testing purposes.
     */
    public DataStorage getDataStorage() {
        return dataStorage;
    }
}
