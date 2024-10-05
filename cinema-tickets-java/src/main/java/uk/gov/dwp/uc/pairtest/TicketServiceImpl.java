package uk.gov.dwp.uc.pairtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.configuration.ConfigurationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.validation.ValidatorService;

public class TicketServiceImpl implements TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;
    private final ConfigurationService configurationService;

    private final ValidatorService validatorService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService,
                             SeatReservationService seatReservationService,
                             ConfigurationService configurationService,
                             ValidatorService validatorService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
        this.configurationService = configurationService;
        this.validatorService = validatorService;
        //loadProperties("ticket_config.properties");
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        try {
            validatorService.validateAccount(accountId);
            validatorService.validateTickets(ticketTypeRequests);

            int totalAmount = calculateTotalAmount(ticketTypeRequests);

            seatReservationService.reserveSeat(accountId, totalAmount);
            ticketPaymentService.makePayment(accountId, totalAmount);

            logger.info("Successfully purchased tickets for account " + accountId);
        } catch (InvalidPurchaseException e) {
            logger.error("Ticket validation failed for account " + accountId, e);
            throw e;
        } catch (Exception e) {
            logger.error("Failed to complete ticket purchase for account " + accountId + e.getMessage());
            throw new InvalidPurchaseException("Failed to complete ticket purchase " + e.getMessage());
        }
    }

    private int calculateTotalAmount(TicketTypeRequest... ticketTypeRequests) {
        int totalAmount = 0;
        for (TicketTypeRequest request : ticketTypeRequests) {
            Integer price = configurationService.getTicketPrices().get(request.getTicketType());
            if (price == null) {
                logger.error("Price not found for ticket type: " + request.getTicketType());
                throw new InvalidPurchaseException("Invalid ticket type: " + request.getTicketType());
            }
            totalAmount += price * request.getNoOfTickets();
        }
        return totalAmount;
    }

    // Other methods remain unchanged...
}

