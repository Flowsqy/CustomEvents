package fr.flowsqy.customevents.event.manager;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.event.queue.EventQueue;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CycleManager {

    private final CustomEventsPlugin plugin;
    private final Locale locale;
    private final TaskManager taskManager;

    public CycleManager(CustomEventsPlugin plugin, Locale locale) {
        this.plugin = plugin;
        this.locale = locale;
        taskManager = new TaskManager();
    }

    public void load() {
        final Calendar now = GregorianCalendar.getInstance(locale);
        final EventQueue eventQueue = plugin.getEventManager().initialize(
                plugin.getLogger(),
                new File(plugin.getDataFolder(), "events"),
                now
        );
        taskManager.initialize(plugin, eventQueue, now);
    }

    public void unload() {
        taskManager.cancel();
    }

    public void reload() {
        unload();
        load();
    }

}
