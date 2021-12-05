package fr.flowsqy.customevents.event.manager;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.event.queue.EventQueue;
import org.bukkit.Bukkit;
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

        launchEvent(plugin, queue);
    }

    private void launchEvent(CustomEventsPlugin plugin, EventQueue queue) {
        Bukkit.getScheduler().runTaskLater(plugin, new TaskRunnable(plugin, queue), queue.getTimeBeforeNextEvent());
    }

    public void cancel() {
        if (isRunning() && !task.isCancelled()) {
            task.cancel();
        }
    }

    public boolean isRunning() {
        return task != null;
    }

    private final class TaskRunnable implements Runnable {

        private final CustomEventsPlugin plugin;
        private final EventQueue queue;

        public TaskRunnable(CustomEventsPlugin plugin, EventQueue queue) {
            this.plugin = plugin;
            this.queue = queue;
        }

        @Override
        public void run() {
            final Event event = queue.poll();
            event.perform();
            launchEvent(plugin, queue);
        }
    }

}
