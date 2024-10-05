import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.configuration.ConfigurationServiceImpl;
import uk.gov.dwp.uc.pairtest.configuration.configsource.ConfigSource;
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
        Properties mockProperties = new Properties();
        mockProperties.setProperty("INFANT_PRICE", "5");
        mockProperties.setProperty("CHILD_PRICE", "10");
        mockProperties.setProperty("ADULT_PRICE", "20");
        mockProperties.setProperty("MAX_TICKETS", "6");

        // Mock behavior for loading properties
        when(mockConfigSource.loadProperties()).thenReturn(mockProperties);

        // Call the method to load properties
        configurationService.loadProperties();

        // Verify ticket prices
        Map<TicketTypeRequest.Type, Integer> ticketPrices = configurationService.getTicketPrices();
        assertEquals(5, ticketPrices.get(TicketTypeRequest.Type.INFANT));
        assertEquals(10, ticketPrices.get(TicketTypeRequest.Type.CHILD));
        assertEquals(20, ticketPrices.get(TicketTypeRequest.Type.ADULT));

        // Verify max tickets
        assertEquals(6, configurationService.getMaxTickets());
    }

    @Test
    void testLoadPropertiesHandlesMissingFile() throws IOException {
        // Mock behavior for loading properties to throw an IOException
        when(mockConfigSource.loadProperties()).thenThrow(new IOException("File not found"));

        // Verify that a ConfigurationException is thrown
        assertThrows(IOException.class, () -> {
            configurationService.loadProperties();
        });
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
        assertThrows(ConfigurationException.class, () -> {
            configurationService.loadProperties();
        });
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
        assertThrows(ConfigurationException.class, () -> {
            configurationService.loadProperties();
        });
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
        assertThrows(ConfigurationException.class, () -> {
            configurationService.loadProperties();
        });
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
        assertThrows(ConfigurationException.class, () -> {
            configurationService.loadProperties();
        });
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
        assertThrows(ConfigurationException.class, () -> {
            configurationService.loadProperties();
        });
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
