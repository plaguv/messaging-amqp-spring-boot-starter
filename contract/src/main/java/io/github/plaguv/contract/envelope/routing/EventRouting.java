package io.github.plaguv.contract.envelope.routing;

import jakarta.annotation.Nonnull;

public record EventRouting(
        EventScope eventScope,
        String eventWildcard
) {
    public EventRouting {
        if (eventScope == null) {
            throw new IllegalArgumentException(
                    "EventRouting attribute 'eventDispatchType' cannot be null"
            );
        }
        if (eventWildcard == null) {
            eventWildcard = "";
        }
        if (eventScope != EventScope.BROADCAST && (eventWildcard.isBlank())) {
            throw new IllegalArgumentException(
                    "EventRouting attribute 'eventWildcard' cannot be null or empty when scope is set to '%s' or '%s'"
                    .formatted(EventScope.GROUP, EventScope.TARGET)
            );
        }
    }

    @Override
    public @Nonnull String toString() {
        return "EventRouting{" +
                "eventDispatchType=" + eventScope +
                ", eventWildcard='" + eventWildcard + '\'' +
                '}';
    }
}