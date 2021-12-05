package fr.flowsqy.customevents.event.queue;

public class EventLink {

    private EventChain event;
    // Time needed to hit this event
    private long time;
    // Next Event
    private EventLink eventLink;

    public EventLink(EventChain event, long time) {
        this.event = event;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public EventChain getEvent() {
        return event;
    }

    public void setEvent(EventChain event) {
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
