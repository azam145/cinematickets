package uk.gov.dwp.uc.pairtest.configuration;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.configuration.configsource.ConfigSource;
import uk.gov.dwp.uc.pairtest.configuration.configsource.FileConfigSource;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.ConfigurationException;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigurationServiceImplTest {

    private ConfigurationServiceImpl configurationService;
    private ConfigSource mockConfigSource;

    @BeforeEach
    void setUp() {
        // Create a mock ConfigSource
        mockConfigSource = mock(ConfigSource.class);
        // Initialize the ConfigurationServiceImpl with the mocked ConfigSource
        configurationService = new ConfigurationServiceImpl(mockConfigSource);
    }

    @Test
    void testLoadPropertiesSuccessfully() throws IOException {
        // Prepare mock properties
        var realConfigSource = new FileConfigSource("ticket_config.properties");
        var configurationService1 = new ConfigurationServiceImpl(realConfigSource);

        // Call the method to load properties
        configurationService1.loadProperties();

        // Verify ticket prices
        Map<TicketTypeRequest.Type, Integer> ticketPrices = configurationService1.getTicketPrices();
        assertEquals(0, ticketPrices.get(TicketTypeRequest.Type.INFANT));
        assertEquals(20, ticketPrices.get(TicketTypeRequest.Type.CHILD));
        assertEquals(25, ticketPrices.get(TicketTypeRequest.Type.ADULT));

        // Verify max tickets
        assertEquals(25, configurationService1.getMaxTickets());
    }

    @Test
    void testLoadPropertiesHandlesMissingFile() {
        // Mock behavior for loading properties to throw an IOException
        var realConfigSource = new FileConfigSource("ticket.properties");
        var configurationService1 = new ConfigurationServiceImpl(realConfigSource);


        // Verify that a ConfigurationException is thrown
        assertThrows(IOException.class, configurationService1::loadProperties);
    }

    @Test
    void testLoadPropertiesHandlesInvalidIntegerProperty() throws IOException {
        // Prepare mock properties with an invalid integer
        Properties mockProperties = new Properties();
        mockProperties.setProperty("INFANT_PRICE", "five"); // Invalid value
        mockProperties.setProperty("CHILD_PRICE", "10");
        mockProperties.setProperty("ADULT_PRICE", "20");
        mockProperties.setProperty("MAX_TICKETS", "6");

        // Mock behavior for loading properties
        when(mockConfigSource.loadProperties()).thenReturn(mockProperties);

        // Verify that a ConfigurationException is thrown
        assertEquals(
                "One or more properties are not valid integers",
                assertThrows(ConfigurationException.class, () -> configurationService.loadProperties()).getMessage()
        );

    }

    @Test
    void testLoadPropertiesHandlesMissingMaxTickets() throws IOException {
        // Prepare mock properties without MAX_TICKETS
        Properties mockProperties = new Properties();
        mockProperties.setProperty("INFANT_PRICE", "5");
        mockProperties.setProperty("CHILD_PRICE", "10");
        mockProperties.setProperty("ADULT_PRICE", "20");

        // Mock behavior for loading properties
        when(mockConfigSource.loadProperties()).thenReturn(mockProperties);

        // Verify that a ConfigurationException is thrown when MAX_TICKETS is missing
        assertEquals(
                "Missing required property: MAX_TICKETS",
                assertThrows(ConfigurationException.class, () -> configurationService.loadProperties()).getMessage()
        );

    }

    @Test
    void testLoadPropertiesHandlesMissingInfantPrice() throws IOException {
        // Prepare mock properties without INFANT_PRICE
        Properties mockProperties = new Properties();
        mockProperties.setProperty("CHILD_PRICE", "10");
        mockProperties.setProperty("ADULT_PRICE", "20");
        mockProperties.setProperty("MAX_TICKETS", "6");

        // Mock behavior for loading properties
        when(mockConfigSource.loadProperties()).thenReturn(mockProperties);

        // Verify that a ConfigurationException is thrown when INFANT_PRICE is missing
        assertEquals(
                "Missing required property: INFANT_PRICE",
                assertThrows(ConfigurationException.class, () -> configurationService.loadProperties()).getMessage()
        );

    }

    @Test
    void testLoadPropertiesHandlesMissingChildPrice() throws IOException {
        // Prepare mock properties without CHILD_PRICE
        Properties mockProperties = new Properties();
        mockProperties.setProperty("INFANT_PRICE", "5");
        mockProperties.setProperty("ADULT_PRICE", "20");
        mockProperties.setProperty("MAX_TICKETS", "6");

        // Mock behavior for loading properties
        when(mockConfigSource.loadProperties()).thenReturn(mockProperties);

        // Verify that a ConfigurationException is thrown when CHILD_PRICE is missing
        assertEquals(
                "Missing required property: CHILD_PRICE",
                assertThrows(ConfigurationException.class, () -> configurationService.loadProperties()).getMessage()
        );


    }

    @Test
    void testLoadPropertiesHandlesMissingAdultPrice() throws IOException {
        // Prepare mock properties without ADULT_PRICE
        Properties mockProperties = new Properties();
        mockProperties.setProperty("INFANT_PRICE", "5");
        mockProperties.setProperty("CHILD_PRICE", "10");
        mockProperties.setProperty("MAX_TICKETS", "6");

        // Mock behavior for loading properties
        when(mockConfigSource.loadProperties()).thenReturn(mockProperties);

        // Verify that a ConfigurationException is thrown when ADULT_PRICE is missing
        assertEquals(
                "Missing required property: ADULT_PRICE",
                assertThrows(ConfigurationException.class, () -> configurationService.loadProperties()).getMessage()
        );
    }

    @Test
    void testGetTicketPricesReturnsCorrectValues() throws IOException {
        // Prepare mock properties
        Properties mockProperties = new Properties();
        mockProperties.setProperty("INFANT_PRICE", "5");
        mockProperties.setProperty("CHILD_PRICE", "10");
        mockProperties.setProperty("ADULT_PRICE", "20");
        mockProperties.setProperty("MAX_TICKETS", "6");

        // Mock behavior for loading properties
        when(mockConfigSource.loadProperties()).thenReturn(mockProperties);

        // Call the method to load properties
        configurationService.loadProperties();

        // Verify that getTicketPrices() returns the correct values
        Map<TicketTypeRequest.Type, Integer> ticketPrices = configurationService.getTicketPrices();
        assertEquals(5, ticketPrices.get(TicketTypeRequest.Type.INFANT));
        assertEquals(10, ticketPrices.get(TicketTypeRequest.Type.CHILD));
        assertEquals(20, ticketPrices.get(TicketTypeRequest.Type.ADULT));
    }

    @Test
    void testGetMaxTicketsReturnsCorrectValue() throws IOException {
        // Prepare mock properties
        Properties mockProperties = new Properties();
        mockProperties.setProperty("INFANT_PRICE", "5");
        mockProperties.setProperty("CHILD_PRICE", "10");
        mockProperties.setProperty("ADULT_PRICE", "20");
        mockProperties.setProperty("MAX_TICKETS", "6");

        // Mock behavior for loading properties
        when(mockConfigSource.loadProperties()).thenReturn(mockProperties);

        // Call the method to load properties
        configurationService.loadProperties();

        // Verify that getMaxTickets() returns the correct value
        assertEquals(6, configurationService.getMaxTickets());
    }
}
