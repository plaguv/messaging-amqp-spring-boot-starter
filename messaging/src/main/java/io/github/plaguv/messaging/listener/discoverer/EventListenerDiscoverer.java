package io.github.plaguv.messaging.listener.discoverer;

import io.github.plaguv.messaging.utlity.helper.Listener;

import java.util.List;

public interface EventListenerDiscoverer {
    List<Listener> getListeners();
}