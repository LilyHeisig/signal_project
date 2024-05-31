package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * I did not change this according to the week 6 task because that would
     * require changing the implementation of the FileDataReader class. I
     * implemented the SimpleWebSocketClient class to implement this interface
     * even if the method signature here is not ideal for that classes'
     * implementation.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;
}
