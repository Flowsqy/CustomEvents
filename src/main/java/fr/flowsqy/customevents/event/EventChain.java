package fr.flowsqy.customevents.event;

import fr.flowsqy.customevents.api.Event;

public record EventChain(Event event, EventDater eventDater) {

    DatedEvent getNextEvent() {
        return eventDater.getNextEvent(event);
    }

}
