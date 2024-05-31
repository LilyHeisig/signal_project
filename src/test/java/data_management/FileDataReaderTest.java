package data_management;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;

public class FileDataReaderTest {
    @Test
    void testFileDataReader() {
        FileDataReader reader = new FileDataReader("src/test/resources/test_data.csv");
        DataStorage storage = new DataStorage();
        assertDoesNotThrow(() -> reader.readData(storage));
    }
    
}
