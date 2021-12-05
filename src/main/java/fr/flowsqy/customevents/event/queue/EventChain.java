package fr.flowsqy.customevents.event.queue;

import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.event.DatedEvent;
import fr.flowsqy.customevents.event.dater.EventDater;

public record EventChain(Event event, EventDater eventDater) {

    public DatedEvent getNextEvent() {
        return eventDater.getNextEvent(event);
    }

}
