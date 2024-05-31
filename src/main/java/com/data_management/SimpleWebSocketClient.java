package com.data_management;

import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class SimpleWebSocketClient extends WebSocketClient implements DataReader {

    private DataStorage dataStorage;

    public SimpleWebSocketClient(URI serverURI) {
		super(serverURI);
	}

    @Override
    public void readData(DataStorage dataStorage) {
        // Store the data storage object
        this.dataStorage = dataStorage;
        // Connect to the WebSocket server and read data
        try {
            connectBlocking();
        } catch (Exception e) {
            System.err.println("An error occurred while connecting to the WebSocket server: " + e.getMessage());
        }
    }

    public void processData(String message) throws Exception {
        // initialise local variables
        long timestamp;
        double measurementValue;
        int patientId;
        String recordType;

        // Process the data received from the WebSocket server, throwing errors
        if (message.isEmpty()) {
            throw new Exception("Invalid message received from WebSocket server");
        }
        message = message.trim();
        String[] parts = message.split("\\s*,\\s*");
        if (parts.length != 4) {
            throw new Exception("Invalid data format received from WebSocket server");
        }
        try {
            patientId = Integer.parseInt(parts[0]);
        } catch (Exception e) {
            throw new Exception("Invalid patient ID format received from WebSocket server");
        }
        try {
            measurementValue = Double.parseDouble(parts[3]);
        } catch (Exception e) {
            throw new Exception("Invalid measurement value format received from WebSocket server");
        }
        try {
            timestamp = Long.parseLong(parts[1]);
        } catch (Exception e) {
            throw new Exception("Invalid timestamp format received from WebSocket server");
        }
        recordType = parts[2];

        // Validate the data
        if (timestamp < 0) {
            throw new Exception("Invalid timestamp received from WebSocket server");
        }
        if (!recordType.equals("Cholesterol") && !recordType.equals("WhiteBloodCells")
                && !recordType.equals("RedBloodCells") && !recordType.equals("Saturation")
                && !recordType.equals("Alert") && !recordType.equals("SystolicPressure")
                && !recordType.equals("DiastolicPressure") && !recordType.equals("ECG")) {
            throw new Exception("Invalid record type received from WebSocket server: " + recordType);
        }

        if (measurementValue < 0) {
            throw new Exception("Invalid measurement value received from WebSocket server");
        }
        if (patientId < 0) {
            throw new Exception("Invalid patient ID received from WebSocket server");
        }

        // Add the data to the data storage
        try {
            dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
        } catch (IllegalArgumentException e) {
            System.err.println("An error occurred while adding the data to the data storage: " + e.getMessage());
        }
    }

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("new connection opened");
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("closed with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(String message) {
		System.out.println("received message: " + message);
        try {
            processData(message);
        } catch (Exception e) {
            System.err.println("An error occurred while processing the data: " + e.getMessage());
        }
	}

	@Override
	public void onMessage(ByteBuffer message) {
		System.out.println("received ByteBuffer");
	}

	@Override
	public void onError(Exception ex) {
		System.err.println("an error occurred:" + ex);
	}

    public DataStorage getDataStorage() {
        return dataStorage;
    }
}
