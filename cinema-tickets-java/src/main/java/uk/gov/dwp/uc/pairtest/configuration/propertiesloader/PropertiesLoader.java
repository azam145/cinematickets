package uk.gov.dwp.uc.pairtest.configuration.propertiesloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

    private final ClassLoader classLoader;

    // Setter to inject a mock logger for testing
    public static void setLogger(Logger mockLogger) {
        logger = mockLogger;
    }

        public ClassLoader getClassLoader() {
        return classLoader;
    }


    // Constructor that accepts a ClassLoader
    public PropertiesLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    // Default constructor that uses the default class loader
    public PropertiesLoader() {
        this.classLoader = getClass().getClassLoader();
    }

    // Load properties from an InputStream
    public Properties loadProperties(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            logger.error("Input stream is null");
            throw new IOException("Input stream is null");
        }
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    // Load properties from a file path using the injected ClassLoader
    public Properties loadPropertiesFromFile(String filePath) throws IOException {
        try (InputStream input = classLoader.getResourceAsStream(filePath)) {
            return loadProperties(input);
        } catch(IOException exception) {
            logger.error("File not found: "+filePath);
            throw new FileNotFoundException("File not found: "+filePath);
        }
    }
}
