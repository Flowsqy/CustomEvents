package fr.flowsqy.customevents.io;

import java.util.Calendar;

public class EventManager {


    private Calendar getNextDay(final Calendar now, int dayOfWeek){
        final Calendar nextDay = (Calendar) now.clone();
        nextDay.add(Calendar.DAY_OF_WEEK, dayOfWeek - now.get(Calendar.DAY_OF_WEEK) % 7);
        return nextDay;
    }

}
