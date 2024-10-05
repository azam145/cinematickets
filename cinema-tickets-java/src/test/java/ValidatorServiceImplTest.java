import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.dwp.uc.pairtest.configuration.ConfigurationService;
import uk.gov.dwp.uc.pairtest.validation.ValidatorServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;


class ValidatorServiceImplTest {

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private ValidatorServiceImpl validatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this); // Initialize mocks
    }

    @Test
    void validateTickets_InvalidQuantity_ThrowsException() {
        TicketTypeRequest invalidRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0); // Invalid ticket quantity

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () ->
                validatorService.validateTickets(invalidRequest));

        assertEquals("Invalid quantity for ticket type: ADULT", exception.getMessage());
    }

    @Test
    void validateTickets_ExceedsMaxTickets_ThrowsException() {
        // Given
        when(configurationService.getMaxTickets()).thenReturn(5); // Mock max tickets

        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 6); // Exceed max

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () ->
                validatorService.validateTickets(adultRequest));

        assertEquals("Cannot purchase more than 5 tickets at a time.", exception.getMessage());
    }

    @Test
    void validateTickets_NoAdultTicket_ThrowsException() {
        // Given
        when(configurationService.getMaxTickets()).thenReturn(5);

        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2); // No adult, only child tickets

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () ->
                validatorService.validateTickets(childRequest));

        assertEquals("Child and Infant tickets require at least one Adult ticket.", exception.getMessage());
    }

    @Test
    void validateTickets_ValidTickets_NoException() {
        // Given
        when(configurationService.getMaxTickets()).thenReturn(5);

        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        assertDoesNotThrow(() -> validatorService.validateTickets(adultRequest, childRequest));
    }

    @Test
    void validateAccount_ValidAccountId_ReturnsTrue() {
        assertTrue(validatorService.validateAccount(1L)); // Valid account ID
    }

    @Test
    void validateAccount_InvalidAccountId_ReturnsFalse() {
        assertFalse(validatorService.validateAccount(0L)); // Invalid account ID (<= 0)
    }
}
