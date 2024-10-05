package uk.gov.dwp.uc.pairtest.configuration.propertiesloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    // Load properties from a file path
    public Properties loadPropertiesFromFile(String filePath) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (input == null) {
                throw new IOException("File not found: " + filePath);
            }
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        }
    }

    // Load properties from an InputStream
    public Properties loadPropertiesFromStream(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    // Safely retrieve an integer property from the properties object
    public int getIntegerProperty(Properties properties, String key) throws NumberFormatException {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new NumberFormatException("Property " + key + " is missing or invalid");
        }
        return Integer.parseInt(value);
    }
}
