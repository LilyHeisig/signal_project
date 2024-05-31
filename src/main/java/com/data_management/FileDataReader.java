package com.data_management;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader{

    String source;

    /**
     * Constructor for the FileDataReader class.
     * 
     * @param source The source directory to read data from
     */
    public FileDataReader(String source) {
        this.source = source;
    }
    @Override
    /**
     * Reads data from a specified source and stores it in the data storage.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    public void readData(DataStorage dataStorage) throws IOException{
        // truncate the --output file:<output_dir> argument from the source
        if (source.startsWith("--output file:")) {
            source = source.substring(14);
        }
        // Read data from a specified source and store it in the data storage
        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int patientId = Integer.parseInt(parts[0]);
                    double measurementValue = Double.parseDouble(parts[1]);
                    String recordType = parts[2];
                    long timestamp = Long.parseLong(parts[3]);
                    dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
                }
            }
        } catch (Exception e) {
            throw new IOException("An error occurred while reading data from the file: " + e.getMessage());
        }
    }
    
}
