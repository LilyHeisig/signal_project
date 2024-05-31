package data_management;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import com.data_management.SimpleWebSocketClient;
import com.cardio_generator.outputs.WebSocketOutputStrategy;
import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.DataManager;
import com.alerts.AlertPublisher;

public class SimpleWebSocketClientTest {

    /**
     * Test method for {@link src.main.java.data_management.SimpleWebSocketClient#SimpleWebSocketClient(java.lang.String)}.
     * This test will check if the constructor is working properly
     * 
     */
   @Test 
    void simpleWebSocketClientConstructorTest() {
        try {
            SimpleWebSocketClient client = new SimpleWebSocketClient(new URI("ws://localhost:8080"));
            assertNotNull(client);
            // close the connection
            client.close();
        } catch (Exception e) {
            System.out.println("Invalid URI Error: " + e.getMessage());
        }
    }

    /**
     * This tests the connection of the server using websocket.org's echo server
     */
   @Test 
    void simpleWebSocketClientConnectToEchoServerTest() {
        try {
            URI uri = new URI("wss://echo.websocket.org");
            SimpleWebSocketClient client = new SimpleWebSocketClient(uri);
            
            System.out.println("Attempting to connect to: " + uri.toString());
            System.out.println("Client is open before connect: " + client.isOpen());

            client.connectBlocking(); // Blocking connect to ensure the connection is established
            
            System.out.println("Client is open after connect: " + client.isOpen());

            assertTrue(client.isOpen());

            client.close();
            System.out.println("Client closed successfully");

        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This test will check if the connection is established with the local
     * server.
     * 
     */
    @Test
    void simpleWebSocketClientConnectTest(){
        try {
            WebSocketOutputStrategy server = new WebSocketOutputStrategy(3503);

            URI uri = new URI("ws://localhost:3503");
            SimpleWebSocketClient client = new SimpleWebSocketClient(uri);
            
            System.out.println("Attempting to connect to: " + uri.toString());
            System.out.println("Client is open before connect: " + client.isOpen());

            client.connectBlocking(); // Blocking connect to ensure the connection is established
            
            System.out.println("Client is open after connect: " + client.isOpen());

            assertTrue(client.isOpen());

            client.close();
            System.out.println("Client closed successfully");

        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test method for {@link src.main.java.data_management.SimpleWebSocketClient#disconnect()}.
     * This test will check if the connection is disconnected
     * 
     */
    @Test
    void simpleWebSocketClientDisconnectTest() {
        try {
            WebSocketOutputStrategy server = new WebSocketOutputStrategy(3503);
            SimpleWebSocketClient client = new SimpleWebSocketClient(new URI("ws://localhost:3503"));
            client.connectBlocking();
            System.out.println("Client is open before disconnect: " + client.isOpen());
            client.close();
            System.out.println("Client is open after disconnect: " + client.isOpen());
            assertFalse(client.isOpen());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Test method for {@link src.main.java.data_management.SimpleWebSocketClient#isConnected()}.
     * This test will check if the client creates a patient record and stores
     * it in the dataStorage (method processData)
     * 
     */
    @Test
    void simpleWebSocketClientProcessDataTest() {
        // Create a new client
        try {
            WebSocketOutputStrategy server = new WebSocketOutputStrategy(3503);

            URI uri = new URI("ws://localhost:3503");
            SimpleWebSocketClient client = new SimpleWebSocketClient(uri);

            client.readData(new DataStorage());
            System.out.println("Client is open: " + client.isOpen());

            // prepare the message
            String message = "1,1632873600,Cholesterol,80";
            client.processData(message);
            System.out.println("Data processed.");

            client.close();
            System.out.println("Client closed successfully");

            PatientRecord record = new PatientRecord(1, 80, "Cholesterol", 1632873600L);

            DataManager dataManager = DataManager.getInstance();

            assertEquals(record.getMeasurementValue(), dataManager.getDataStorage().getRecords(record.getPatientId(), record.getTimestamp(), (record.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
            assertEquals(record.getTimestamp(), dataManager.getDataStorage().getRecords(record.getPatientId(), record.getTimestamp(), (record.getTimestamp()+1)).get(0).getTimestamp(), "The record is not stored in the dataStorage");
            assertEquals(record.getPatientId(), dataManager.getDataStorage().getRecords(record.getPatientId(), record.getTimestamp(), (record.getTimestamp()+1)).get(0).getPatientId(), "The record is not stored in the dataStorage");
            assertEquals(record.getRecordType(), dataManager.getDataStorage().getRecords(record.getPatientId(), record.getTimestamp(), (record.getTimestamp()+1)).get(0).getRecordType(), "The record is not stored in the dataStorage");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Test method for {@link src.main.java.data_management.SimpleWebSocketClient#isConnected()}.
     * This test will check if the client creates a patient record and stores
     * it in the dataStorage when it is sent by the server. This test will check
     * the whole functionality of the class.
     * 
     */
    @Test
    void simpleWebSocketClientFullCommunicationTest() {
        try {
            WebSocketOutputStrategy server = new WebSocketOutputStrategy(3503);

            URI uri = new URI("ws://localhost:3503");
            SimpleWebSocketClient client = new SimpleWebSocketClient(uri);

            client.readData(new DataStorage());
            System.out.println("Client is open: " + client.isOpen());

            // send the message
            server.output(1, 1632873600, "Cholesterol", "80");

            Thread.sleep(1000);

            client.close();
            System.out.println("Client closed successfully");

            server.closeServer();
            System.out.println("Server closed successfully");

            PatientRecord record = new PatientRecord(1, 80, "Cholesterol", 1632873600L);
            DataManager dataManager = DataManager.getInstance();
            System.out.println("fetched the data manager");

            assertNotNull(dataManager.getDataStorage());
            System.out.println("data storage is not null");

            assertEquals(record.getMeasurementValue(), dataManager.getDataStorage().getRecords(record.getPatientId(), record.getTimestamp(), (record.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
            assertEquals(record.getTimestamp(), dataManager.getDataStorage().getRecords(record.getPatientId(), record.getTimestamp(), (record.getTimestamp()+1)).get(0).getTimestamp(), "The record is not stored in the dataStorage");
            assertEquals(record.getPatientId(), dataManager.getDataStorage().getRecords(record.getPatientId(), record.getTimestamp(), (record.getTimestamp()+1)).get(0).getPatientId(), "The record is not stored in the dataStorage");
            assertEquals(record.getRecordType(), dataManager.getDataStorage().getRecords(record.getPatientId(), record.getTimestamp(), (record.getTimestamp()+1)).get(0).getRecordType(), "The record is not stored in the dataStorage");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Test to see if the client can handle a lot of messages.
     */
    @Test
    void simpleWebSocketClientMultipleMessagesTest() {
        try {
            WebSocketOutputStrategy server = new WebSocketOutputStrategy(3503);

            URI uri = new URI("ws://localhost:3503");
            SimpleWebSocketClient client = new SimpleWebSocketClient(uri);

            client.readData(new DataStorage());
            System.out.println("Client is open: " + client.isOpen());

            // send the message
            server.output(1, 1632873600, "Cholesterol", "80");
            server.output(1, 1632873601, "Cholesterol", "81");
            server.output(1, 1632873602, "Cholesterol", "82");
            server.output(1, 1632873603, "Cholesterol", "83");
            server.output(1, 1632873604, "Cholesterol", "84");
            server.output(1, 1632873605, "Cholesterol", "85");
            server.output(1, 1632873606, "Cholesterol", "86");
            server.output(1, 1632873607, "Cholesterol", "87");
            server.output(1, 1632873608, "Cholesterol", "88");
            server.output(1, 1632873609, "Cholesterol", "89");

            PatientRecord record1 = new PatientRecord(1, 80, "Cholesterol", 1632873600L);
            PatientRecord record2 = new PatientRecord(1, 81, "Cholesterol", 1632873601L);
            PatientRecord record3 = new PatientRecord(1, 82, "Cholesterol", 1632873602L);
            PatientRecord record4 = new PatientRecord(1, 83, "Cholesterol", 1632873603L);
            PatientRecord record5 = new PatientRecord(1, 84, "Cholesterol", 1632873604L);
            PatientRecord record6 = new PatientRecord(1, 85, "Cholesterol", 1632873605L);
            PatientRecord record7 = new PatientRecord(1, 86, "Cholesterol", 1632873606L);
            PatientRecord record8 = new PatientRecord(1, 87, "Cholesterol", 1632873607L);
            PatientRecord record9 = new PatientRecord(1, 88, "Cholesterol", 1632873608L);
            PatientRecord record10 = new PatientRecord(1, 89, "Cholesterol", 1632873609L);

            client.close();
            System.out.println("Client closed successfully");

            server.closeServer();
            System.out.println("Server closed successfully");

            DataManager dataManager = DataManager.getInstance();

            assertEquals(record1.getMeasurementValue(), dataManager.getDataStorage().getRecords(record1.getPatientId(), record1.getTimestamp(), (record1.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
            assertEquals(record2.getMeasurementValue(), dataManager.getDataStorage().getRecords(record2.getPatientId(), record2.getTimestamp(), (record2.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
            assertEquals(record3.getMeasurementValue(), dataManager.getDataStorage().getRecords(record3.getPatientId(), record3.getTimestamp(), (record3.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
            assertEquals(record4.getMeasurementValue(), dataManager.getDataStorage().getRecords(record4.getPatientId(), record4.getTimestamp(), (record4.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
            assertEquals(record5.getMeasurementValue(), dataManager.getDataStorage().getRecords(record5.getPatientId(), record5.getTimestamp(), (record5.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
            assertEquals(record6.getMeasurementValue(), dataManager.getDataStorage().getRecords(record6.getPatientId(), record6.getTimestamp(), (record6.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
            assertEquals(record7.getMeasurementValue(), dataManager.getDataStorage().getRecords(record7.getPatientId(), record7.getTimestamp(), (record7.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
            assertEquals(record8.getMeasurementValue(), dataManager.getDataStorage().getRecords(record8.getPatientId(), record8.getTimestamp(), (record8.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
            assertEquals(record9.getMeasurementValue(), dataManager.getDataStorage().getRecords(record9.getPatientId(), record9.getTimestamp(), (record9.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
            assertEquals(record10.getMeasurementValue(), dataManager.getDataStorage().getRecords(record10.getPatientId(), record10.getTimestamp(), (record10.getTimestamp()+1)).get(0).getMeasurementValue(), "The record is not stored in the dataStorage");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Test to see if an alert is triggered.
     */
    @Test
    void simpleWebSocketClientAlertTest() {
        try {
            WebSocketOutputStrategy server = new WebSocketOutputStrategy(3503);

            URI uri = new URI("ws://localhost:3503");
            SimpleWebSocketClient client = new SimpleWebSocketClient(uri);

            client.readData(new DataStorage());
            System.out.println("Client is open: " + client.isOpen());

            long time = System.currentTimeMillis();

            // send the message
            server.output(1, time-5000, "ECGData", "80");
            server.output(1, time-4000, "ECGData", "80");
            server.output(1, time-3000, "ECGData", "80");
            server.output(1, time-2000, "ECGData", "80");
            server.output(1, time-1000, "ECGData", "80");
            server.output(1, time, "ECGData", "885");


            client.close();
            System.out.println("Client closed successfully");

            server.closeServer();
            System.out.println("Server closed successfully");

            // check if an alert has been generated on the debug console

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}