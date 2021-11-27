package fr.flowsqy.customevents.event;

import fr.flowsqy.customevents.api.Event;

public record DatedEvent(long timeBeforeEvent, Event event) {
}
