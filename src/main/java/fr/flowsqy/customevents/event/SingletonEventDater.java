package fr.flowsqy.customevents.event;

import fr.flowsqy.customevents.api.Event;

import java.util.Calendar;

public class SingletonEventDater implements EventDater {

    private final Calendar calendar;

    public SingletonEventDater(Calendar now, WeekDate weekDate) {
        this.calendar = EventDater.getNextDay(now, weekDate);
    }

    @Override
    public DatedEvent getNextEvent(Event event) {
        calendar.add(Calendar.DAY_OF_WEEK, EventDater.DAY_IN_WEEK);
        return new DatedEvent(calendar.getTimeInMillis(), event);
    }

    @Override
    public DatedEvent getCurrentEvent(Event event) {
        return new DatedEvent(calendar.getTimeInMillis(), event);
    }
}
