package fr.flowsqy.customevents.event.manager;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.event.queue.EventQueue;

import java.io.File;
import java.util.*;

public class CycleManager {

    private final CustomEventsPlugin plugin;
    private final Locale locale;
    private final TaskManager taskManager;
    private final List<Event> events;

    public CycleManager(CustomEventsPlugin plugin, Locale locale) {
        this.plugin = plugin;
        this.locale = locale;
        taskManager = new TaskManager();
        events = new LinkedList<>();
    }

    public void load() {
        final Calendar now = GregorianCalendar.getInstance(locale);
        final EventQueue eventQueue = plugin.getEventManager().initialize(
                plugin.getLogger(),
                new File(plugin.getDataFolder(), "events"),
                now
        );
        if (eventQueue != null) {
            for (Event event : eventQueue) {
                events.add(event);
            }
        }
        taskManager.initialize(plugin, eventQueue, now);
    }

    public void unload() {
        taskManager.cancel();
        events.clear();
    }

    public void reload() {
        unload();
        load();
    }

    public List<Event> getEvents() {
        return events;
    }
}
