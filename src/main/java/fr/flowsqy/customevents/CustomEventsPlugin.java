package fr.flowsqy.customevents;

import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.api.EventDeserializer;
import fr.flowsqy.customevents.event.manager.EventManager;
import fr.flowsqy.customevents.event.manager.TaskManager;
import fr.flowsqy.customevents.event.queue.EventQueue;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomEventsPlugin extends JavaPlugin {

    private EventManager eventManager;
    private TaskManager taskManager;

    @Override
    public void onLoad() {
        eventManager = new EventManager();
    }

    @Override
    public void onEnable() {
        final Logger logger = getLogger();
        final File dataFolder = getDataFolder();

        if (!checkDataFolder(dataFolder)) {
            logger.warning("Can not write in the directory : " + dataFolder.getAbsolutePath());
            logger.warning("Disable the plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final YamlConfiguration configuration = initFile(dataFolder, "config.yml");
        final Locale locale = initLocal(configuration, logger);

        final Calendar now = GregorianCalendar.getInstance(locale);
        final EventQueue eventQueue = eventManager.initialize(getLogger(), new File(dataFolder, "events"), now);
        taskManager = new TaskManager();
        taskManager.initialize(this, eventQueue, now);
    }

    @Override
    public void onDisable() {
        taskManager.cancel();
    }

    private boolean checkDataFolder(File dataFolder) {
        if (dataFolder.exists())
            return dataFolder.canWrite();
        return dataFolder.mkdirs();
    }

    private YamlConfiguration initFile(File dataFolder, String fileName) {
        final File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getResource(fileName)), file.toPath());
            } catch (IOException ignored) {
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    private Locale initLocal(YamlConfiguration configuration, Logger logger) {
        final String localeFieldName = configuration.getString("timezone");
        Locale locale = null;
        if (localeFieldName != null && !localeFieldName.isBlank()) {
            for (Field field : Locale.class.getDeclaredFields()) {
                if (
                        field.getType() == Locale.class
                                && Modifier.isStatic(field.getModifiers())
                                && field.getName().equalsIgnoreCase(localeFieldName)
                ) {
                    try {
                        locale = (Locale) field.get(null);
                    } catch (IllegalAccessException e) {
                        logger.log(Level.SEVERE, "Can't access locale field", e);
                        break;
                    }
                }
            }
        }
        if (locale == null) {
            locale = Locale.getDefault();
            logger.warning("Unable to load the timezone from local name, set it to default (" + locale.toString() + ")");
        }

        return locale;
    }

}