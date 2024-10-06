package uk.gov.dwp.uc.pairtest.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.uc.pairtest.configuration.configsource.ConfigSource;
import uk.gov.dwp.uc.pairtest.exception.ConfigurationException;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class);

    private int maxTickets;
    private final Map<TicketTypeRequest.Type, Integer> ticketPrices;
    private final ConfigSource configSource;

    public ConfigurationServiceImpl(ConfigSource configSource) {
        this.configSource = configSource;
        this.ticketPrices = new HashMap<>();
    }

    @Override
    public void loadProperties() throws IOException {
        Properties properties = configSource.loadProperties();
        parseAndSetProperties(properties);
    }

    private void parseAndSetProperties(Properties properties) {
        logger.info("Parsing property values");

        // Validate properties before setting them
        validateProperties(properties);

        try {
            ticketPrices.put(TicketTypeRequest.Type.INFANT, Integer.parseInt(properties.getProperty("INFANT_PRICE")));
            ticketPrices.put(TicketTypeRequest.Type.CHILD, Integer.parseInt(properties.getProperty("CHILD_PRICE")));
            ticketPrices.put(TicketTypeRequest.Type.ADULT, Integer.parseInt(properties.getProperty("ADULT_PRICE")));
            maxTickets = Integer.parseInt(properties.getProperty("MAX_TICKETS"));
            logger.info("Properties loaded successfully: {}", properties);
        } catch (NumberFormatException e) {
            logger.error("Error parsing property values", e);
            throw new ConfigurationException("Error parsing property values", e);
        }
    }

    private void validateProperties(Properties properties) {
        // Check if required properties are present
        if (properties.getProperty("INFANT_PRICE") == null) {
            logger.error("Missing required property: INFANT_PRICE");
            throw new ConfigurationException("Missing required property: INFANT_PRICE");
        }
        if (properties.getProperty("CHILD_PRICE") == null) {
            logger.error("Missing required property: CHILD_PRICE");
            throw new ConfigurationException("Missing required property: CHILD_PRICE");
        }
        if (properties.getProperty("ADULT_PRICE") == null) {
            throw new ConfigurationException("Missing required property: ADULT_PRICE");
        }
        if (properties.getProperty("MAX_TICKETS") == null) {
            logger.error("Missing required property: MAX_TICKETS");
            throw new ConfigurationException("Missing required property: MAX_TICKETS");
        }

        // Validate that properties can be parsed as integers
        try {
            Integer.parseInt(properties.getProperty("INFANT_PRICE"));
            Integer.parseInt(properties.getProperty("CHILD_PRICE"));
            Integer.parseInt(properties.getProperty("ADULT_PRICE"));
            Integer.parseInt(properties.getProperty("MAX_TICKETS"));
        } catch (NumberFormatException e) {
            logger.error("One or more properties are not valid integers", e);
            throw new ConfigurationException("One or more properties are not valid integers", e);
        }
    }

    @Override
    public Map<TicketTypeRequest.Type, Integer> getTicketPrices() {
        return ticketPrices;
    }

    @Override
    public int getMaxTickets() {
        return maxTickets;
    }
}
