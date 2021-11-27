package fr.flowsqy.customevents.event;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class EventQueue {

    private final long start;
    private final EventLink first;

    public EventQueue(List<DatedEvent> events, long start) {
        Objects.requireNonNull(events);
        if (events.isEmpty()) {
            throw new IllegalArgumentException("events list should not be empty");
        }
        events.sort(Comparator.comparingLong(DatedEvent::timeBeforeEvent));

        final Iterator<DatedEvent> eventIterator = events.listIterator();
        DatedEvent datedEvent = eventIterator.next();

        if (datedEvent.timeBeforeEvent() < start) {
            throw new IllegalArgumentException("start time must be inferior to the time before the first event");
        }

        long currentTime = this.start = start;
        EventLink previousEventLink = first = new EventLink(
                datedEvent.timeBeforeEvent() - currentTime,
                datedEvent.event()
        );
        currentTime += first.getTime();

        while (eventIterator.hasNext()) {
            datedEvent = eventIterator.next();
            final EventLink currentEventLink = new EventLink(
                    datedEvent.timeBeforeEvent() - currentTime,
                    datedEvent.event()
            );
            currentTime += currentEventLink.getTime();
            previousEventLink.setEventLink(currentEventLink);
            previousEventLink = currentEventLink;
        }
    }

    public long getTimeBeforeNextEvent() {
        return getTimeBeforeNextEvent(System.currentTimeMillis());
    }

    public long getTimeBeforeNextEvent(long currentTime) {
        return start + first.getTime() - currentTime;
    }

    public DatedEvent getNextEvent() {
        return getNextEvent(System.currentTimeMillis());
    }

    // Peek
    public DatedEvent getNextEvent(long currentTime) {
        return new DatedEvent(getTimeBeforeNextEvent(currentTime), first.getEvent());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (EventLink link = this.first; link != null; link = link.getEventLink()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(link);
        }
        sb.append("]");
        return "EventQueue" + sb;
    }

}
