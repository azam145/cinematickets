package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public interface ValidatorService {
    void validateTickets(TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException;
    boolean validateAccount(Long accountId);
}
