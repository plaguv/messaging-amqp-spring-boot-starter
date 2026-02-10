package io.github.plaguv.contract.event.logistic;

import io.github.plaguv.contract.event.Event;
import io.github.plaguv.contract.event.EventDomain;

@Event(domain = EventDomain.LOGISTIC)
public record OrderSendOutEvent(
        long storeId
) {

    public OrderSendOutEvent {
        if (storeId < 1) {
            throw new IllegalArgumentException("OrderSendOutEvent attribute 'storeId' cannot be < 1");
        }
    }
}