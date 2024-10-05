package uk.gov.dwp.uc.pairtest.configuration;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.io.IOException;
import java.util.Map;

public interface ConfigurationService {
    Map<TicketTypeRequest.Type, Integer> getTicketPrices();
    void loadProperties() throws IOException;
    int getMaxTickets();
}
