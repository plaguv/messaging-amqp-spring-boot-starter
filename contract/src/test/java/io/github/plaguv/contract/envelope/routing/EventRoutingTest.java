package io.github.plaguv.contract.envelope.routing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventRoutingTest {

    @Test
    @DisplayName("Constructor should throw if null parameter in constructor")
    void throwsOnNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventRouting(null,  null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventRouting(EventScope.TARGET,  null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventRouting(EventScope.GROUP,  null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventRouting(null, "test"));
    }

    @Test
    @DisplayName("Constructor should allow null wildcard if and only if the scope is a broadcast")
    void allowEmptyWildcardOnBroadcast() {
        Assertions.assertDoesNotThrow(
                () -> new EventRouting(EventScope.BROADCAST, null));
    }
}