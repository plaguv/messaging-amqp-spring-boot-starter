package io.github.plaguv.contract.envelope.metadata;

import io.github.plaguv.contract.event.EventVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventVersionTest {

    private EventVersion eventVersion;

    @Test
    @DisplayName("Constructor should reject negative numbers for major, minor and patch")
    void throwsOnNegativeNumbers() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventVersion(-1, 0, 0));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventVersion(0, -1, 0));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new EventVersion(0, 0, -1));
    }

    @Test
    @DisplayName("Should default to zero, if number isn't present")
    void defaultsToZero() {
        eventVersion = new EventVersion(1);
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(0, eventVersion.minor());
        Assertions.assertEquals(0, eventVersion.patch());

        eventVersion = new EventVersion(1, 2);
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(2, eventVersion.minor());
        Assertions.assertEquals(0, eventVersion.patch());

        eventVersion = new EventVersion(1, 2, 3);
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(2, eventVersion.minor());
        Assertions.assertEquals(3, eventVersion.patch());
    }

    @Test
    @DisplayName("Constructor should return a correct EventVersion in the valueOf(int) method")
    void valueOfIntParses() {
        eventVersion = EventVersion.valueOf(1);
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(0, eventVersion.minor());
        Assertions.assertEquals(0, eventVersion.patch());

        eventVersion = EventVersion.valueOf(1, 2);
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(2, eventVersion.minor());
        Assertions.assertEquals(0, eventVersion.patch());

        eventVersion = EventVersion.valueOf(1,2, 3);
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(2, eventVersion.minor());
        Assertions.assertEquals(3, eventVersion.patch());
    }

    @Test
    @DisplayName("Constructor should throw an exception when an invalid string format gets input")
    void valueOfStringThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventVersion.valueOf("a"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventVersion.valueOf("1a"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventVersion.valueOf("1.a"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventVersion.valueOf("1.."));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventVersion.valueOf("1.1;1"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventVersion.valueOf("1.1.1.1"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventVersion.valueOf("-1"));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventVersion.valueOf(null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> EventVersion.valueOf(" "));
    }

    @Test
    @DisplayName("Constructor should return a correct EventVersion in the valueOf(string) method")
    void valueOfStringParses() {
        eventVersion = EventVersion.valueOf("1");
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(0, eventVersion.minor());
        Assertions.assertEquals(0, eventVersion.patch());

        eventVersion = EventVersion.valueOf("1.2");
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(2, eventVersion.minor());
        Assertions.assertEquals(0, eventVersion.patch());

        eventVersion = EventVersion.valueOf("1.2.3");
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(2, eventVersion.minor());
        Assertions.assertEquals(3, eventVersion.patch());

        eventVersion = EventVersion.valueOf("1_2_3");
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(2, eventVersion.minor());
        Assertions.assertEquals(3, eventVersion.patch());

        eventVersion = EventVersion.valueOf("1-2-3");
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(2, eventVersion.minor());
        Assertions.assertEquals(3, eventVersion.patch());

        eventVersion = EventVersion.valueOf("1;2;3");
        Assertions.assertEquals(1, eventVersion.major());
        Assertions.assertEquals(2, eventVersion.minor());
        Assertions.assertEquals(3, eventVersion.patch());
    }
}