package fr.flowsqy.customevents.event.manager;

import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.api.EventData;
import fr.flowsqy.customevents.api.EventDeserializer;
import fr.flowsqy.customevents.event.WeekDate;
import fr.flowsqy.customevents.event.dater.EventDater;
import fr.flowsqy.customevents.event.queue.EventChain;
import fr.flowsqy.customevents.event.queue.EventQueue;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class EventManager {

    private Map<String, EventDeserializer> deserializers;

    public EventManager() {
        this.deserializers = new HashMap<>();
    }

    /**
     * Register an event deserializer
     *
     * @param type         The event identifier
     * @param deserializer The {@link EventDeserializer} which will load the event configuration
     * @param force        Whether this event should replace an existing event deserialize with the same identifier
     * @return The already registered {@link EventDeserializer},
     */
    public EventDeserializer register(String type, EventDeserializer deserializer, boolean force) {
        if (force) {
            return deserializers.put(type, deserializer);
        }
        return deserializers.putIfAbsent(type, deserializer);
    }

    /**
     * Load all event configurations in the specified directory
     *
     * @param logger      The plugin {@link Logger} to warn if a configuration is invalid
     * @param eventFolder The directory which contains all events configurations
     * @param now         A {@link Calendar} instance which the right timezone
     * @return The {@link EventQueue} filled by all the events. {@code null} if there is no event
     */
    public EventQueue initialize(Logger logger, File eventFolder, Calendar now) {
        if (!eventFolder.exists() && !eventFolder.mkdirs()) {
            logger.warning(eventFolder.getAbsolutePath() + " can not be created");
            return null;
        }
        if (!eventFolder.canRead() || !eventFolder.isDirectory()) {
            logger.warning(eventFolder.getAbsolutePath() + " is not a readable directory");
            return null;
        }
        final File[] files = eventFolder.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        final List<EventChain> chains = new ArrayList<>();
        for (File file : files) {
            final EventChain chain = initEvent(file, logger, now);
            if (chain != null) {
                chains.add(chain);
            }
        }
        deserializers = null;
        return chains.isEmpty() ? null : new EventQueue(chains, now.getTimeInMillis());
    }

    /**
     * Load an event configuration file
     *
     * @param file   The yaml configuration of the event
     * @param logger The plugin {@link Logger} to warn if the configuration is invalid
     * @param now    A {@link Calendar} instance which the right timezone
     * @return An {@link EventChain} representing the configuration. {@code null} if the configuration is invalid
     */
    private EventChain initEvent(File file, Logger logger, Calendar now) {
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        final String fileName = file.getName();
        final ConfigurationSection dateSection = configuration.getConfigurationSection("date");
        if (dateSection == null) {
            logger.warning(fileName + " does not contain 'date' section");
            return null;
        }
        final EventDater dater = initDate(dateSection, fileName, logger, now);
        if (dater == null) {
            return null;
        }
        final String eventType = configuration.getString("type");
        if (eventType == null) {
            logger.warning(fileName + " try to load an event without type");
            return null;
        }
        final ConfigurationSection dataSection = configuration.getConfigurationSection("data");
        if (dataSection == null) {
            logger.warning(fileName + "does not contain 'data' section");
            return null;
        }
        final EventData eventData = initData(dataSection, fileName, logger);
        if (eventData == null) {
            return null;
        }
        final EventDeserializer deserializer = deserializers.get(eventType);
        if (deserializer == null) {
            logger.warning(fileName + " try to register an event that can not be deserialized (" + eventType + ")");
            return null;
        }
        final ConfigurationSection eventSection = configuration.getConfigurationSection("event");
        if (eventSection == null) {
            logger.warning(fileName + " does not comport 'event' section");
            return null;
        }
        final Event event = deserializer.deserialize(eventSection, logger, fileName, eventData);
        if (event == null) {
            return null;
        }
        return new EventChain(event, dater);
    }


    /**
     * Load a date yaml configuration section
     *
     * @param datesSection The 'date' {@link ConfigurationSection}
     * @param fileName     The name of the file for logging purpose
     * @param logger       The plugin {@link Logger} to warn if a date is invalid
     * @param now          A {@link Calendar} instance which the right timezone
     * @return An {@link EventDater} set to the configuration dates
     */
    private EventDater initDate(ConfigurationSection datesSection, String fileName, Logger logger, Calendar now) {
        final List<WeekDate> dates = new ArrayList<>();
        for (String key : datesSection.getKeys(false)) {
            final ConfigurationSection dateSection = datesSection.getConfigurationSection(key);
            if (dateSection == null) {
                continue;
            }
            final int dayOfWeek = dateSection.getInt("day");
            if (dayOfWeek > 7 || dayOfWeek < 1) {
                logger.warning(fileName + " : date." + key + ".day must be between 0 and 6");
                continue;
            }
            final int hour = dateSection.getInt("hour");
            if (hour > 23 || hour < 0) {
                logger.warning(fileName + " : date." + key + ".hour must be between 0 and 23");
                continue;
            }
            final int minute = dateSection.getInt("minute");
            if (minute > 59 || minute < 0) {
                logger.warning(fileName + " : date." + key + ".minute must be between 0 and 59");
                continue;
            }
            final WeekDate date = new WeekDate(dayOfWeek, hour, minute);
            dates.add(date);
        }
        if (dates.isEmpty()) {
            logger.warning(fileName + " try to register an event without valid date");
            return null;
        }
        return EventDater.getDater(now, dates);
    }

    /**
     * Load a date yaml configuration section
     *
     * @param dataSection The 'data' {@link ConfigurationSection}
     * @param fileName    The name of the file for logging purpose
     * @param logger      The plugin {@link Logger} to warn if a date is invalid
     * @return An {@link EventData} filled by the configuration data
     */
    private EventData initData(ConfigurationSection dataSection, String fileName, Logger logger) {
        String commandId = dataSection.getString("command-id");
        if (commandId == null || (commandId = commandId.trim()).isEmpty()) {
            logger.warning(fileName + " has an empty command-id");
            return null;
        }

        return new EventData(commandId);
    }

}
