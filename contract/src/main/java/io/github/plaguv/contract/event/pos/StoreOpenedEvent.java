package io.github.plaguv.contract.event.pos;

import io.github.plaguv.contract.event.Event;
import io.github.plaguv.contract.event.EventDomain;

@Event(domain = EventDomain.STORE)
public record StoreOpenedEvent(
        long storeId
) {

    public StoreOpenedEvent {
        if (storeId < 1) {
            throw new IllegalArgumentException("StoreOpenedEvents attribute 'storeId' cannot be < 1");
        }
    }
}