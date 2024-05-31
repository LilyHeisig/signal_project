package com.cardio_generator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.lang.Thread;

public class WebSocketOutputStrategy implements OutputStrategy {

    private WebSocketServer server;

    public WebSocketOutputStrategy(int port) {
        server = new SimpleWebSocketServer(new InetSocketAddress(port));
        System.out.println("WebSocket server created on port: " + port + ", listening for connections...");
        server.start();
    }

    public void closeServer() {
        try {
            server.stop();
        } catch (Exception e) {
            System.out.println("Error stopping server: " + e.getMessage());
        }
    }

    /**
     * Outputs the patient data to all connected WebSocket clients.
     * I did not change the output formatting here (as was instruected
     * in the week 6 assignment) because I don't think it's necessary.
     * I implemented the Client to handle the data just as it is output
     * here.
     * 
     * @param patientId The ID of the patient
     * @param timestamp The timestamp of the data
     * @param label     The label of the data
     * @param data      The data to be output
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        // Simulate some processing time
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted `-´ : " + e.getMessage());
        }
        String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
        // Broadcast the message to all connected clients
        for (WebSocket conn : server.getConnections()) {
            conn.send(message);
        }
    }

    private static class SimpleWebSocketServer extends WebSocketServer {

        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            System.out.println("New connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used in this context
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
        }

        @Override
        public void onStart() {
            System.out.println("Server started successfully");
        }
    }
}
