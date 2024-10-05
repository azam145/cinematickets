package uk.gov.dwp.uc.pairtest.exception;

public class InvalidPurchaseException extends RuntimeException {
    public InvalidPurchaseException(String message) {
        super(message);  // Pass the message to the superclass constructor
    }
}
