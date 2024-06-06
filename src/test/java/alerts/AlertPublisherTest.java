package alerts;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;

import com.staff_devices.StaffDevice;
import com.alerts.Alert;
import com.alerts.AlertPublisher;

public class AlertPublisherTest {

    private AlertPublisher alertPublisher;

    @BeforeEach
    public void setUp() {
        alertPublisher = AlertPublisher.getInstance();
        // Reset the singleton instance for isolated tests
        alertPublisher = Mockito.spy(alertPublisher);
    }

    @Test
    public void testGetInstance() {
        AlertPublisher instance1 = AlertPublisher.getInstance();
        AlertPublisher instance2 = AlertPublisher.getInstance();

        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "Both instances should be the same");
    }

    @Test
    public void testSubscribe() {
        StaffDevice device = mock(StaffDevice.class);

        alertPublisher.subscribe(device);

        List<StaffDevice> subscribers = alertPublisher.getSubscribers();
        assertTrue(subscribers.contains(device), "Device should be subscribed");
    }

    @Test
    public void testUnsubscribe() {
        StaffDevice device = mock(StaffDevice.class);

        alertPublisher.subscribe(device);
        alertPublisher.unsubscribe(device);

        List<StaffDevice> subscribers = alertPublisher.getSubscribers();
        assertFalse(subscribers.contains(device), "Device should be unsubscribed");
    }

    @Test
    public void testPublishAlert() {
        StaffDevice device1 = mock(StaffDevice.class);
        StaffDevice device2 = mock(StaffDevice.class);
        Alert alert = mock(Alert.class);

        alertPublisher.subscribe(device1);
        alertPublisher.subscribe(device2);

        alertPublisher.publishAlert(alert);

        verify(device1).displayAlert(alert);
        verify(device2).displayAlert(alert);
    }
}
