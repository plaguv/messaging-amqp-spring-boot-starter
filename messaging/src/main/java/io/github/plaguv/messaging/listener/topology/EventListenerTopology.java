package io.github.plaguv.messaging.listener.topology;

import io.github.plaguv.messaging.utlity.helper.Listener;
import org.springframework.amqp.core.Declarables;

import java.util.Collection;

public interface EventListenerTopology {
    Declarables getDeclarablesFromListeners(Collection<Listener> listeners);
}