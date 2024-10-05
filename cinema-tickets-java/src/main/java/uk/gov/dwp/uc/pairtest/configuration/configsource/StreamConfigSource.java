package uk.gov.dwp.uc.pairtest.configuration.configsource;

import uk.gov.dwp.uc.pairtest.configuration.propertiesloader.PropertiesLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StreamConfigSource implements ConfigSource {
    private InputStream inputStream;

    public StreamConfigSource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public Properties loadProperties() throws IOException {
        return new PropertiesLoader().loadPropertiesFromStream(inputStream);
    }
}