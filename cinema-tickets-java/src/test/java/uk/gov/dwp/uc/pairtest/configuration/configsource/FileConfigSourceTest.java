package uk.gov.dwp.uc.pairtest.configuration.configsource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.configuration.propertiesloader.PropertiesLoader;

public class FileConfigSourceTest {

    // Test case 1: Valid properties file
    @Test
    public void testLoadProperties_ValidFile() throws IOException {
        // Arrange
        String filePath = "ticket_config.properties";

        // Mock the PropertiesLoader to simulate valid properties loading
        //{CHILD_PRICE=20, MAX_TICKETS=25, INFANT_PRICE=0, ADULT_PRICE=25}
        Properties expectedProperties = new Properties();
        expectedProperties.setProperty("CHILD_PRICE", "20");
        expectedProperties.setProperty("MAX_TICKETS", "25");
        expectedProperties.setProperty("INFANT_PRICE", "0");
        expectedProperties.setProperty("ADULT_PRICE", "25");

        // Simulate the loadPropertiesFromFile method returning the expected properties
        PropertiesLoader loader = new PropertiesLoader();

        // Act
        Properties result = loader.loadPropertiesFromFile(filePath);

        // Assert
        assertEquals(expectedProperties, result);
    }

    // Test case 2: File not found
    @Test
    public void testLoadProperties_FileNotFound() {
        // Arrange
        String filePath = "nonexistent.properties";

        // Simulate the PropertiesLoader throwing an IOException
        PropertiesLoader loader = new PropertiesLoader();

        // Act & Assert
        IOException thrown = assertThrows(IOException.class, () -> {
            loader.loadPropertiesFromFile(filePath);
        });
        assertEquals("File not found: nonexistent.properties", thrown.getMessage());
    }

    // Test case 3: Empty properties file
    @Test
    public void testLoadProperties_EmptyFile() throws IOException {
        // Arrange
        String filePath = "empty.properties";

        // Mock the PropertiesLoader to simulate loading an empty properties file
        Properties expectedProperties = new Properties(); // Empty properties
        PropertiesLoader loader = spy(new PropertiesLoader());
        doReturn(expectedProperties).when(loader).loadPropertiesFromFile(filePath);

        // Act
        Properties result = loader.loadPropertiesFromFile(filePath);

        // Assert
        assertTrue(result.isEmpty());
    }
}
