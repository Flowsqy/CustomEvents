package fr.flowsqy.customevents.event.manager;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.event.queue.EventQueue;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Calendar;

public class TaskManager {

    private BukkitTask task;

    /**
     * Initialize the {@link EventQueue} and load the task to fire the events
     *
     * @param plugin The {@link CustomEventsPlugin} instance to create the tasks
     * @param queue  The {@link EventQueue} that contains the events
     * @param now    A {@link Calendar} with the right timezone
     */
    public void initialize(CustomEventsPlugin plugin, EventQueue queue, Calendar now) {
        if (queue == null || isRunning()) {
            return;
        }

        while (queue.getTimeBeforeNextEvent(now.getTimeInMillis()) < 0) {
            queue.poll();
        }

        launchEvent(plugin, queue);
    }

    /**
     * Launch the task for the next event
     *
     * @param plugin The {@link CustomEventsPlugin} instance to create the task
     * @param queue  The {@link EventQueue} that contains the events
     */
    private void launchEvent(CustomEventsPlugin plugin, EventQueue queue) {
        task = Bukkit.getScheduler().runTaskLater(
                plugin,
                new TaskRunnable(plugin, queue),
                queue.getTimeBeforeNextEvent() / 50 // Switch from millisecond to ticks
        );
    }

    /**
     * Cancel the current task
     */
    public void cancel() {
        if (isRunning() && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
    }

    /**
     * Whether the task is running
     *
     * @return {@code true} if the task is running, false otherwise
     */
    public boolean isRunning() {
        return task != null;
    }

    /**
     * The runnable used by the task
     */
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
