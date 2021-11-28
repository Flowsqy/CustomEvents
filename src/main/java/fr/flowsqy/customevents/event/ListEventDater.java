package fr.flowsqy.customevents.event;

import fr.flowsqy.customevents.api.Event;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class ListEventDater implements EventDater {

    private final WeekDate[] dates;
    private Calendar current;
    private int cursor;

    public ListEventDater(Calendar now, List<WeekDate> weekDates) {
        if (weekDates.size() < 2) {
            throw new IllegalArgumentException("There must be 2 or more dates");
        }

        // Create date chain
        final Comparator<WeekDate> dayComparator = Comparator.comparingInt(WeekDate::dayOfWeek);
        final Comparator<WeekDate> hourComparator = Comparator.comparingInt(WeekDate::hour);
        final Comparator<WeekDate> minuteComparator = Comparator.comparingInt(WeekDate::minute);
        final Comparator<WeekDate> weekDateComparator = (first, second) -> {
            if (first.dayOfWeek() == second.dayOfWeek()) {
                if (first.hour() == second.hour()) {
                    return minuteComparator.compare(first, second);
                }
                return hourComparator.compare(first, second);
            }
            return dayComparator.compare(first, second);
        };
        weekDates.sort(weekDateComparator);

        this.dates = weekDates.toArray(new WeekDate[0]);

        // Init on the nearest date
        long delta = Long.MAX_VALUE;
        Calendar nearestDate = null;
        int indexOfMinimum = -1;
        for (int index = 0; index < dates.length; index++) {
            final WeekDate date = dates[index];
            final Calendar next = EventDater.getNextDay(now, date);
            final long currentDelta = next.getTimeInMillis() - now.getTimeInMillis();
            if (currentDelta < delta) {
                indexOfMinimum = index;
                nearestDate = next;
                delta = currentDelta;
            }
        }
        this.cursor = indexOfMinimum;
        this.current = nearestDate;
    }

    @Override
    public DatedEvent getNextEvent(Event event) {
        cursor++;
        if (cursor >= dates.length) {
            cursor = 0;
        }
        current = EventDater.getNextDay(current, dates[cursor]);
        return new DatedEvent(current.getTimeInMillis(), event);
    }

    @Override
    public DatedEvent getCurrentEvent(Event event) {
        return new DatedEvent(current.getTimeInMillis(), event);
    }

}
