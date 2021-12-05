package fr.flowsqy.customevents.event.dater;

import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.event.DatedEvent;
import fr.flowsqy.customevents.event.WeekDate;

import java.util.Calendar;

public class SingletonEventDater implements EventDater {

    private final Calendar calendar;

    public SingletonEventDater(Calendar now, WeekDate weekDate) {
        this.calendar = EventDater.getNextDay(now, weekDate);
    }

    @Override
    public DatedEvent getNextEvent(Event event) {
        calendar.add(Calendar.DAY_OF_WEEK, EventDater.DAY_IN_WEEK);
        return getCurrentEvent(event);
    }

    @Override
    public DatedEvent getCurrentEvent(Event event) {
        return new DatedEvent(getCurrentInMillis(), event);
    }

    @Override
    public long getCurrentInMillis() {
        return calendar.getTimeInMillis();
    }
}
