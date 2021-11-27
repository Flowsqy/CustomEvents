package fr.flowsqy.customevents.event;

import fr.flowsqy.customevents.api.Event;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class EventQueue {

    private long start;
    private EventLink first;

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

    public Event poll(Function<Event, DatedEvent> nextEventFunction) {
        return poll(nextEventFunction, System.currentTimeMillis());
    }

    public Event poll(Function<Event, DatedEvent> nextEventFunction, long now) {
        final DatedEvent nextSameEvent = nextEventFunction.apply(first.getEvent());
        Objects.requireNonNull(nextSameEvent, "The same next event can not be null");
        if (nextSameEvent.timeBeforeEvent() < first.getTime() + start) {
            throw new IllegalArgumentException("The next event of the same type can not be before the original");
        }
        // Insert the new event
        add(nextSameEvent);

        final EventLink nextEvent = first.getEventLink();
        // Normally impossible because of the call to the add method
        Objects.requireNonNull(nextEvent, "Next event in event queue can not be null");
        final Event event = first.getEvent();
        start += first.getTime();
        first = nextEvent;
        return event;
    }

    private void add(DatedEvent nextSameEvent) {
        // Put at the beginning
        if (nextSameEvent.timeBeforeEvent() < first.getTime() + start) {
            final EventLink newEventLink = new EventLink(
                    nextSameEvent.timeBeforeEvent() - start,
                    nextSameEvent.event()
            );
            newEventLink.setEventLink(first);
            first = newEventLink;
            return;
        }

        // Else insert after another EventLink

        // Find the event link that will be before the new one
        EventLink eventLink = first;
        long totalTime = start + eventLink.getTime();

        // Total time before next event  ->  current sum of the time + time before next event from current event link
        // While the next event is before the new one
        while (eventLink.getEventLink() != null && (totalTime + eventLink.getEventLink().getTime()) <= nextSameEvent.timeBeforeEvent()) {
            // Pick next event
            eventLink = eventLink.getEventLink();
            // Set the sum of the time to the next event
            totalTime += eventLink.getTime();
        }

        final EventLink nextEvent = eventLink.getEventLink();
        final EventLink newEventLink = new EventLink(
                nextSameEvent.timeBeforeEvent() - totalTime,
                nextSameEvent.event()
        );
        eventLink.setEventLink(newEventLink);
        if (nextEvent != null) {
            newEventLink.setEventLink(nextEvent);
            nextEvent.setTime(nextEvent.getTime() - newEventLink.getTime());
        }
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
