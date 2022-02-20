package fr.flowsqy.customevents.event.queue;

import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.event.DatedEvent;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class EventQueue implements Iterable<Event> {

    private long start;
    private EventLink first;

    /**
     * Create an {@link EventQueue}
     *
     * @param events The list of all {@link EventChain} that will compose this queue
     * @param start  The start time, in milliseconds
     */
    public EventQueue(List<EventChain> events, long start) {
        Objects.requireNonNull(events);
        if (events.isEmpty()) {
            throw new IllegalArgumentException("events list should not be empty");
        }
        events.sort(Comparator.comparingLong(chain -> chain.eventDater().getCurrentInMillis()));

        final Iterator<EventChain> eventIterator = events.listIterator();
        EventChain eventChain = eventIterator.next();

        if (eventChain.eventDater().getCurrentInMillis() < start) {
            throw new IllegalArgumentException("start time must be inferior to the time before the first event");
        }

        long currentTime = this.start = start;
        EventLink previousEventLink = first = new EventLink(
                eventChain,
                eventChain.eventDater().getCurrentInMillis() - currentTime
        );
        currentTime += first.getTime();

        while (eventIterator.hasNext()) {
            eventChain = eventIterator.next();
            final EventLink currentEventLink = new EventLink(
                    eventChain,
                    eventChain.eventDater().getCurrentInMillis() - currentTime
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
        return new DatedEvent(getTimeBeforeNextEvent(currentTime), first.getEvent().event());
    }

    /**
     * Take the current {@link Event} and replace it in the queue at the next date
     *
     * @return The first {@link Event} of this queue
     */
    public Event poll() {
        final DatedEvent nextSameEvent = first.getEvent().getNextEvent();
        Objects.requireNonNull(nextSameEvent, "The same next event can not be null");
        if (nextSameEvent.timeBeforeEvent() < first.getTime() + start) {
            throw new IllegalArgumentException("The next event of the same type can not be before the original");
        }
        // Insert the new event
        add(nextSameEvent, first.getEvent());

        final EventLink nextEvent = first.getEventLink();
        // Normally impossible because of the call to the add method
        Objects.requireNonNull(nextEvent, "Next event in event queue can not be null");
        final Event event = first.getEvent().event();
        start += first.getTime();
        first = nextEvent;
        return event;
    }

    /**
     * Add an event in the queue
     *
     * @param nextSameEvent The {@link DatedEvent} to add
     * @param chain         The {@link EventChain} to update
     */
    private void add(DatedEvent nextSameEvent, EventChain chain) {
        // Put at the beginning
        if (nextSameEvent.timeBeforeEvent() < first.getTime() + start) {
            final EventLink newEventLink = new EventLink(
                    chain,
                    nextSameEvent.timeBeforeEvent() - start
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
                chain,
                nextSameEvent.timeBeforeEvent() - totalTime
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

    @Override
    public Iterator<Event> iterator() {
        return new EventQueueItr(first);
    }

    private final static class EventQueueItr implements Iterator<Event> {

        private EventLink current;

        public EventQueueItr(EventLink current) {
            this.current = current;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Event next() {
            final Event event = current.getEvent().event();
            current = current.getEventLink();
            return event;
        }
    }

}
