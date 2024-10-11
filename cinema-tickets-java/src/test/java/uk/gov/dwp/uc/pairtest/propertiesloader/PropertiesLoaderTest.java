package uk.gov.dwp.uc.pairtest.propertiesloader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.gov.dwp.uc.pairtest.configuration.propertiesloader.PropertiesLoader;

public class PropertiesLoaderTest {

    private PropertiesLoader propertiesLoader;

    @Mock
    private Logger mockLogger;

    private ClassLoader mockClassLoader;

    @BeforeEach
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Mock the ClassLoader
        mockClassLoader = mock(ClassLoader.class);

        // Create a PropertiesLoader instance with the mock ClassLoader
        propertiesLoader = new PropertiesLoader(mockClassLoader);
        PropertiesLoader.setLogger(mockLogger);
    }

    // Test case 1: Valid InputStream
    @Test
    public void testLoadProperties_ValidInputStream() throws IOException {
        // Arrange: Simulate a valid InputStream with properties data
        InputStream inputStream = new ByteArrayInputStream("key1=value1\nkey2=value2".getBytes());

        // Act: Load properties
        Properties result = propertiesLoader.loadProperties(inputStream);

        // Assert: Check that the properties are loaded correctly
        assertEquals("value1", result.getProperty("key1"));
        assertEquals("value2", result.getProperty("key2"));

        // Verify that logger was not called for errors
        verifyNoInteractions(mockLogger);
    }

    // Test case 2: Null InputStream
    @Test
    public void testLoadProperties_NullInputStream() {
        // Act & Assert: Ensure IOException is thrown when InputStream is null
        IOException thrown = assertThrows(IOException.class, () -> {
            propertiesLoader.loadProperties(null);
        });

        assertEquals("Input stream is null", thrown.getMessage());

        // Verify that logger logs the correct error message
        verify(mockLogger).error("Input stream is null");
    }

    // Test case 3: Load properties from valid file path
    @Test
    public void testLoadPropertiesFromFile_ValidFile() throws IOException {
        // Arrange: Simulate a valid InputStream from a file path
        String filePath = "valid.properties";
        InputStream inputStream = new ByteArrayInputStream("key1=value1\nkey2=value2".getBytes());
        when(mockClassLoader.getResourceAsStream(filePath)).thenReturn(inputStream);

        // Act: Load properties from file
        Properties result = propertiesLoader.loadPropertiesFromFile(filePath);

        // Assert: Verify the properties loaded correctly
        assertEquals("value1", result.getProperty("key1"));
        assertEquals("value2", result.getProperty("key2"));

        // Verify no logger errors occurred
        verifyNoInteractions(mockLogger);
    }

    // Test case 4: File not found
    @Test
    public void testLoadPropertiesFromFile_FileNotFound() {
        // Arrange: Simulate a missing file scenario
        String filePath = "nonexistent.properties";
        when(mockClassLoader.getResourceAsStream(filePath)).thenReturn(null);

        // Act & Assert: Ensure IOException is thrown when the file is not found
        IOException thrown = assertThrows(IOException.class, () -> {
            propertiesLoader.loadPropertiesFromFile(filePath);
        });

        assertEquals("File not found: " + filePath, thrown.getMessage());

        // Verify logger logs the correct error message
        verify(mockLogger).error("File not found: " + filePath);
    }

    // Test case 5: Empty properties file
    @Test
    public void testLoadPropertiesFromFile_EmptyFile() throws IOException {
        // Arrange: Simulate an empty properties file
        String filePath = "empty.properties";
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        when(mockClassLoader.getResourceAsStream(filePath)).thenReturn(inputStream);

        // Act: Load properties from file
        Properties result = propertiesLoader.loadPropertiesFromFile(filePath);

        // Assert: Ensure the properties are empty
        assertTrue(result.isEmpty());

        // Verify no logger errors occurred
        verifyNoInteractions(mockLogger);
    }

    // Test case 6: Valid constructor with custom ClassLoader
    @Test
    public void testConstructorWithCustomClassLoader() {
        // Act: Create PropertiesLoader with a custom ClassLoader
        PropertiesLoader loader = new PropertiesLoader(mockClassLoader);

        // Assert: Ensure the classLoader is set correctly
        assertEquals(mockClassLoader, loader.getClassLoader());
    }

    // Test case 7: Default constructor uses default class loader
    @Test
    public void testDefaultConstructorUsesDefaultClassLoader() {
        // Act: Create PropertiesLoader using default constructor
        PropertiesLoader loader = new PropertiesLoader();

        // Assert: Ensure the default class loader is used
        assertEquals(PropertiesLoader.class.getClassLoader(), loader.getClassLoader());
    }
}
