
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.configuration.ConfigurationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.validation.ValidatorService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class TicketServiceImplTest {

    @Mock
    private TicketPaymentService ticketPaymentService;

    @Mock
    private SeatReservationService seatReservationService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private ValidatorService validatorService;

    @InjectMocks
    private TicketServiceImpl ticketServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPurchaseTickets_Success() throws InvalidPurchaseException {
        // Arrange
        Long accountId = 12345L;
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childTicket = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        // Mock configuration for ticket prices
        Map<TicketTypeRequest.Type, Integer> ticketPrices = new HashMap<>();
        ticketPrices.put(TicketTypeRequest.Type.ADULT, 20);
        ticketPrices.put(TicketTypeRequest.Type.CHILD, 10);

        when(configurationService.getTicketPrices()).thenReturn(ticketPrices);

        // Act
        ticketServiceImpl.purchaseTickets(accountId, adultTicket, childTicket);

        // Assert
        int expectedTotalAmount = (2 * 20) + (1 * 10); // 50
        verify(validatorService).validateAccount(accountId);
        verify(validatorService).validateTickets(adultTicket, childTicket);
        verify(seatReservationService).reserveSeat(accountId, expectedTotalAmount);
        verify(ticketPaymentService).makePayment(accountId, expectedTotalAmount);
    }

    @Test
    void testPurchaseTickets_ValidationFails_ThrowsInvalidPurchaseException() throws InvalidPurchaseException {
        // Arrange
        Long accountId = 12345L;
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        doThrow(new InvalidPurchaseException("Invalid account")).when(validatorService).validateAccount(accountId);

        // Act & Assert
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () -> {
            ticketServiceImpl.purchaseTickets(accountId, adultTicket);
        });

        assertEquals("Invalid account", exception.getMessage());
        verify(validatorService).validateAccount(accountId);
        verify(validatorService, never()).validateTickets(any());
        verify(seatReservationService, never()).reserveSeat(anyLong(), anyInt());
        verify(ticketPaymentService, never()).makePayment(anyLong(), anyInt());
    }

    @Test
    void testPurchaseTickets_SeatReservationFails_ThrowsException() throws InvalidPurchaseException {
        // Arrange
        Long accountId = 12345L;
        TicketTypeRequest adultTicket = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        Map<TicketTypeRequest.Type, Integer> ticketPrices = new HashMap<>();
        ticketPrices.put(TicketTypeRequest.Type.ADULT, 20);
        when(configurationService.getTicketPrices()).thenReturn(ticketPrices);

        doThrow(new RuntimeException("Seat reservation failed")).when(seatReservationService)
                .reserveSeat(accountId, 40);

        // Act & Assert
        Exception exception = assertThrows(InvalidPurchaseException.class, () -> {
            ticketServiceImpl.purchaseTickets(accountId, adultTicket);
        });

        assertEquals("Failed to complete ticket purchase Seat reservation failed", exception.getMessage());
        verify(validatorService).validateAccount(accountId);
        verify(validatorService).validateTickets(adultTicket);
        verify(seatReservationService).reserveSeat(accountId, 40);
        verify(ticketPaymentService, never()).makePayment(anyLong(), anyInt());
    }
}
