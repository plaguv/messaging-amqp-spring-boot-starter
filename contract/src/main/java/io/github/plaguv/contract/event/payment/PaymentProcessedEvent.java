package io.github.plaguv.contract.event.payment;

import io.github.plaguv.contract.event.Event;
import io.github.plaguv.contract.event.EventDomain;

@Event(domain = EventDomain.PAYMENT)
public record PaymentProcessedEvent(
        float paymentId
) {

    public PaymentProcessedEvent {
        if (paymentId < 1) {
            throw new IllegalArgumentException("PaymentProcessedEvent attribute 'paymentId' cannot be < 1");
        }
    }
}