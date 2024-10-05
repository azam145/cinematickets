package uk.gov.dwp.uc.pairtest.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.uc.pairtest.configuration.ConfigurationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

public class ValidatorServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ValidatorServiceImpl.class);

    private final ConfigurationService configurationService;

    public ValidatorServiceImpl(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void validateTickets(TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        int totalTickets = 0;
        boolean adultTicketPurchased = false;

        for (TicketTypeRequest request : ticketTypeRequests) {
            if (request.getNoOfTickets() <= 0) {
                logger.error("Invalid quantity for ticket type: " + request.getTicketType());
                throw new InvalidPurchaseException("Invalid quantity for ticket type: " + request.getTicketType());
            }

            totalTickets += request.getNoOfTickets();
            if (request.getTicketType() == ADULT) {
                adultTicketPurchased = true;
            }
        }

        if (totalTickets > configurationService.getMaxTickets()) {
            logger.error("Cannot purchase more than " + configurationService.getMaxTickets() + " tickets at a time.");
            throw new InvalidPurchaseException("Cannot purchase more than " + configurationService.getMaxTickets() + " tickets at a time.");
        }

        if (!adultTicketPurchased) {
            for (TicketTypeRequest request : ticketTypeRequests) {
                if (request.getTicketType() == CHILD || request.getTicketType() == INFANT) {
                    logger.error("Child and Infant tickets require at least one Adult ticket.");
                    throw new InvalidPurchaseException("Child and Infant tickets require at least one Adult ticket.");
                }
            }
        }
    }

    public boolean validateAccount(Long accountId) {
        return accountId > 0 ? true : false;
    }

}
