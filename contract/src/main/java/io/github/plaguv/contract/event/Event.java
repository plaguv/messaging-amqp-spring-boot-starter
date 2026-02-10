package io.github.plaguv.contract.event;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Event {
    EventDomain domain();
    String version() default "1.0.0";
}