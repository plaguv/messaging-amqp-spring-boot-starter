package io.github.plaguv.contract.event.pos;

import io.github.plaguv.contract.event.Event;
import io.github.plaguv.contract.event.EventDomain;

@Event(domain = EventDomain.STORE)
public record StoreClosedEvent(
        long storeId
) {

    public StoreClosedEvent {
        if (storeId < 1) {
            throw new IllegalArgumentException("StoreClosedEvents attribute 'storeId' cannot be < 1");
        }
    }
}