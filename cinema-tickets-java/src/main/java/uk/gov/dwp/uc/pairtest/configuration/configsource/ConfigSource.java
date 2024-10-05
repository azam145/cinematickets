package uk.gov.dwp.uc.pairtest.configuration.configsource;

import java.io.IOException;
import java.util.Properties;

public interface ConfigSource {
    Properties loadProperties() throws IOException;
}
