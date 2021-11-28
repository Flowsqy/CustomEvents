package fr.flowsqy.customevents.event;

import fr.flowsqy.customevents.api.Event;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public interface EventDater {

    int DAY_IN_WEEK = 7;

    static Calendar getNextDay(final Calendar now, WeekDate weekDate) {
        final Calendar nextDay = (Calendar) now.clone();
        nextDay.add(Calendar.DAY_OF_WEEK, weekDate.dayOfWeek() - now.get(Calendar.DAY_OF_WEEK) % 7);
        nextDay.set(Calendar.HOUR_OF_DAY, weekDate.hour());
        nextDay.set(Calendar.MINUTE, weekDate.minute());

        nextDay.set(Calendar.SECOND, 0);
        nextDay.set(Calendar.MILLISECOND, 0);

        while (now.getTimeInMillis() > nextDay.getTimeInMillis()) {
            nextDay.add(Calendar.DAY_OF_WEEK, DAY_IN_WEEK);
        }
        return nextDay;
    }

    static EventDater getDater(Calendar now, List<WeekDate> dates) {
        Objects.requireNonNull(now, "now calendar");
        Objects.requireNonNull(dates, "dates list");
        if (dates.isEmpty()) {
            throw new IllegalArgumentException("Date list can not be empty");
        }
        if (dates.size() == 1) {
            return new SingletonEventDater(now, dates.get(0));
        }
        return new ListEventDater(now, dates);
    }

    DatedEvent getNextEvent(Event event);

    DatedEvent getCurrentEvent(Event event);

    long getCurrentInMillis();

}
