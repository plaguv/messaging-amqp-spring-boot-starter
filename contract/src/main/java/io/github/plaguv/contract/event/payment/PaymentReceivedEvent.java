package io.github.plaguv.contract.event.payment;

import io.github.plaguv.contract.event.Event;
import io.github.plaguv.contract.event.EventDomain;

@Event(domain = EventDomain.PAYMENT)
public record PaymentReceivedEvent(
        float amount
) {

    public PaymentReceivedEvent {
        if (amount < 1) {
            throw new IllegalArgumentException("PaymentReceivedEvents attribute 'amount' cannot be < 1");
        }
    }
}