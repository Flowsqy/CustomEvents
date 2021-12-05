package fr.flowsqy.customevents.event.manager;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.event.queue.EventQueue;
import org.bukkit.scheduler.BukkitTask;

import java.util.Calendar;

public class TaskManager {

    private BukkitTask task;

    public void initialize(CustomEventsPlugin plugin, EventQueue queue, Calendar now) {
        if (queue == null || isRunning()) {
            return;
        }

        while (queue.getTimeBeforeNextEvent(now.getTimeInMillis()) < 0) {
            queue.poll();
        }

        // TODO Launch events
    }

    public void cancel() {
        if (isRunning() && !task.isCancelled()) {
            task.cancel();
        }
    }

    public boolean isRunning() {
        return task != null;
    }

}
