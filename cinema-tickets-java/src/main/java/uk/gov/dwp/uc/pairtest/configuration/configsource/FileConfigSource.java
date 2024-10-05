package uk.gov.dwp.uc.pairtest.configuration.configsource;

import uk.gov.dwp.uc.pairtest.configuration.propertiesloader.PropertiesLoader;

import java.io.IOException;
import java.util.Properties;

public class FileConfigSource implements ConfigSource {
    private String filePath;

    public FileConfigSource(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Properties loadProperties() throws IOException {
        return new PropertiesLoader().loadPropertiesFromFile(filePath);
    }
}