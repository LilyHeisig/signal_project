package data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.data_management.DataManager;
import com.data_management.DataStorage;
import com.data_management.SimpleWebSocketClient;

public class SimpleWebSocketClientTest {

    private SimpleWebSocketClient client;
    private DataManager dataManager;
    private DataStorage dataStorage;

    @BeforeEach
    public void setUp() throws Exception {
        URI uri = new URI("ws://localhost:8080");
        dataManager = mock(DataManager.class);

        // Use reflection to set the singleton instance of DataManager to the mocked instance
        setMockDataManagerInstance(dataManager);

        client = spy(new SimpleWebSocketClient(uri));
    }

    private void setMockDataManagerInstance(DataManager mockInstance) throws Exception {
        java.lang.reflect.Field instance = DataManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, mockInstance);
    }

    @Test
    public void testReadData() {
        dataStorage = mock(DataStorage.class);
        client.readData(dataStorage);
        try {
            verify(client).connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testProcessData_validData() throws Exception {
        String message = "1, 1627849923000, Cholesterol, 200.5";
        client.processData(message);
        verify(dataManager, times(1)).manageData(1, 200.5, "Cholesterol", 1627849923000L);
    }

    @Test
    public void testProcessData_invalidMessageFormat() {
        assertThrows(Exception.class, () -> client.processData(""));
        assertThrows(Exception.class, () -> client.processData("1, 1627849923000, Cholesterol"));
    }

    @Test
    public void testProcessData_invalidPatientId() {
        assertThrows(Exception.class, () -> client.processData("abc, 1627849923000, Cholesterol, 200.5"));
    }

    @Test
    public void testProcessData_invalidTimestamp() {
        assertThrows(Exception.class, () -> client.processData("1, abc, Cholesterol, 200.5"));
        assertThrows(Exception.class, () -> client.processData("1, -1627849923000, Cholesterol, 200.5"));
    }

    @Test
    public void testProcessData_invalidMeasurementValue() {
        assertThrows(Exception.class, () -> client.processData("1, 1627849923000, Cholesterol, abc"));
        assertThrows(Exception.class, () -> client.processData("1, 1627849923000, Cholesterol, -200.5"));
    }

    @Test
    public void testProcessData_invalidRecordType() {
        assertThrows(Exception.class, () -> client.processData("1, 1627849923000, InvalidType, 200.5"));
    }

    @Test
    public void testOnMessage_validMessage() throws Exception {
        String message = "1, 1627849923000, Cholesterol, 200.5";
        client.onMessage(message);
        verify(dataManager, times(1)).manageData(1, 200.5, "Cholesterol", 1627849923000L);
    }

    @Test
    public void testOnMessage_invalidMessage() {
        String message = "invalid message";
        client.onMessage(message);
        verify(dataManager, never()).manageData(anyInt(), anyDouble(), anyString(), anyLong());
    }

    @Test
    public void testOnOpen() {
        ServerHandshake handshake = mock(ServerHandshake.class);
        client.onOpen(handshake);
        // You can use System.out stream to assert the printed messages or just check the console output manually.
    }

    @Test
    public void testOnClose() {
        client.onClose(1000, "normal closure", true);
        // You can use System.out stream to assert the printed messages or just check the console output manually.
    }

    @Test
    public void testOnError() {
        Exception ex = new Exception("Test exception");
        client.onError(ex);
        // You can use System.err stream to assert the printed messages or just check the console output manually.
    }
}
