package fr.flowsqy.customevents.io;

import java.util.Calendar;

public class EventManager {

    private long getMillisBefore(final Calendar now, int dayOfWeek, int hours, int minutes) {
        final Calendar nextDay = (Calendar) now.clone();
        nextDay.add(Calendar.DAY_OF_WEEK, dayOfWeek - now.get(Calendar.DAY_OF_WEEK) % 7);
        nextDay.set(Calendar.HOUR_OF_DAY, hours);
        nextDay.set(Calendar.MINUTE, minutes);

        return nextDay.getTimeInMillis() - now.getTimeInMillis();
    }

}
