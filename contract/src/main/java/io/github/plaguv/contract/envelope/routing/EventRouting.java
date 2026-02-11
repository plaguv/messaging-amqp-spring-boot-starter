package io.github.plaguv.contract.envelope.routing;

public record EventRouting(
        EventScope scope,
        String wildcard
) {
    public EventRouting {
        if (scope == null) {
            throw new IllegalArgumentException("EventRouting attribute 'scope' cannot be null");
        }
        if (scope != EventScope.BROADCAST && (wildcard == null || wildcard.isBlank())) {
            throw new IllegalArgumentException(
                    "EventRouting attribute 'wildcard' cannot be null or empty when scope is set to '%s' or '%s'"
                    .formatted(EventScope.GROUP, EventScope.TARGET)
            );
        }
    }
}