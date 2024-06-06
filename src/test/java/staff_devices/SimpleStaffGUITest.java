package staff_devices;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;

import com.alerts.Alert;
import com.staff_devices.SimpleStaffGUI;

public class SimpleStaffGUITest {

    private SimpleStaffGUI simpleStaffGUI;
    private Alert alertMock;
    
    @BeforeEach
    public void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> simpleStaffGUI = new SimpleStaffGUI());
        alertMock = mock(Alert.class);
    }
    
    @Test
    public void testDisplayAlert() throws Exception {
        // Prepare the mock alert
        when(alertMock.getMessage()).thenReturn("Test Alert");

        // Use SwingUtilities.invokeAndWait to ensure the GUI update is completed before verification
        SwingUtilities.invokeAndWait(() -> {
            simpleStaffGUI.displayAlert(alertMock);
        });

        // Retrieve the text in the JTextArea using SwingUtilities.invokeAndWait
        String actualText = invokeAndWait(() -> simpleStaffGUI.getTextArea().getText());

        // Verify that the text area contains the alert message
        String expectedText = "Test Alert\n";
        assertEquals(expectedText, actualText, "Alert message should be displayed in the text area");
    }
    
    @Test
    public void testInitialGUIComponents() throws Exception {
        // Check if the frame is not null
        assertNotNull(invokeAndWait(() -> simpleStaffGUI.getFrame()), "Frame should be initialized");
        
        // Check if the text area is not null
        assertNotNull(invokeAndWait(() -> simpleStaffGUI.getTextArea()), "Text area should be initialized");
        
        // Check if the frame is visible
        assertTrue(invokeAndWait(() -> simpleStaffGUI.getFrame().isVisible()), "Frame should be visible");
        
        // Check if the text area is not editable
        assertFalse(invokeAndWait(() -> simpleStaffGUI.getTextArea().isEditable()), "Text area should not be editable");
    }
    
    @Test
    public void testFrameProperties() throws Exception {
        // Check frame title
        assertEquals("Alert", invokeAndWait(() -> simpleStaffGUI.getFrame().getTitle()), "Frame title should be 'Alert'");
        
        // Check default close operation
        assertEquals(JFrame.EXIT_ON_CLOSE, invokeAndWait(() -> simpleStaffGUI.getFrame().getDefaultCloseOperation()), "Default close operation should be EXIT_ON_CLOSE");
        
        // Check frame size
        Dimension expectedSize = new Dimension(800, 300);
        Dimension actualSize = invokeAndWait(() -> simpleStaffGUI.getFrame().getSize());
        
        assertEquals(expectedSize, actualSize, "Frame size should be 800x300");
    }
    
    @Test
    public void testTextAreaFont() throws Exception {
        // Check the font of the text area
        Font expectedFont = new Font("Arial", Font.PLAIN, 16);
        Font actualFont = invokeAndWait(() -> simpleStaffGUI.getTextArea().getFont());
        
        assertEquals(expectedFont, actualFont, "Text area font should be Arial, plain, size 16");
    }
    
    // Utility method to handle SwingUtilities.invokeAndWait with a Callable
    private <T> T invokeAndWait(Callable<T> callable) throws Exception {
        final Object[] result = new Object[1];
        final Exception[] exception = new Exception[1];

        SwingUtilities.invokeAndWait(() -> {
            try {
                result[0] = callable.call();
            } catch (Exception e) {
                exception[0] = e;
            }
        });

        if (exception[0] != null) {
            throw exception[0];
        }
        return (T) result[0];
    }
}
