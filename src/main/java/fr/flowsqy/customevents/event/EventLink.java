package fr.flowsqy.customevents.event;

import fr.flowsqy.customevents.api.Event;

public class EventLink {

    private Event event;
    private long time;
    private EventLink eventLink;

    public EventLink(long time, Event event) {
        this.time = time;
        this.event = event;
    }

    public EventLink(long time, Event event, EventLink eventLink) {
        this.time = time;
        this.event = event;
        this.eventLink = eventLink;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventLink getEventLink() {
        return eventLink;
    }

    public void setEventLink(EventLink eventLink) {
        this.eventLink = eventLink;
    }

    @Override
    public String toString() {
        return "EventLink{" +
                "time=" + time +
                ", event=" + event +
                '}';
    }
}
