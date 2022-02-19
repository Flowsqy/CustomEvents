package fr.flowsqy.customevents;

import fr.flowsqy.customevents.command.CommandManager;
import fr.flowsqy.customevents.event.manager.CycleManager;
import fr.flowsqy.customevents.event.manager.EventManager;
import fr.flowsqy.customevents.io.Messages;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomEventsPlugin extends JavaPlugin {

    private EventManager eventManager;
    private Messages messages;
    private CycleManager cycleManager;

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

        final YamlConfiguration configuration = initConfiguration(dataFolder, "config.yml");
        final Locale locale = initLocal(configuration, logger);

        cycleManager = new CycleManager(this, locale);


        messages = new Messages(
                initConfiguration(dataFolder, "messages.yml"),
                "&7[&5CustomEvents&7]&f"
        );

        new CommandManager(this);

        Bukkit.getScheduler().runTask(this, cycleManager::load);
    }

    @Override
    public void onDisable() {
        cycleManager.unload();
    }

    /**
     * Ensure the data folder exists and the plugin has the permissions to write in it
     *
     * @param dataFolder The data folder to check
     * @return Whether the folder is usable
     */
    private boolean checkDataFolder(File dataFolder) {
        if (dataFolder.exists())
            return dataFolder.canWrite();
        return dataFolder.mkdirs();
    }

    /**
     * Copy or load a plugin file in the given data folder if it does not exist
     *
     * @param dataFolder The {@link File} instance representing the data folder
     * @param fileName   The name of the file to load
     * @return The {@link File} instance of the loaded file
     */
    private File initFile(File dataFolder, String fileName) {
        final File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getResource(fileName)), file.toPath());
            } catch (IOException ignored) {
            }
        }

        return file;
    }

    /**
     * Initialize a file with {@link #initFile(File, String)} and load it as a {@link YamlConfiguration}
     *
     * @param dataFolder The {@link File} instance representing the data folder
     * @param fileName   The name of the file to load
     * @return The {@link YamlConfiguration} stored in the file
     */
    private YamlConfiguration initConfiguration(File dataFolder, String fileName) {
        return YamlConfiguration.loadConfiguration(initFile(dataFolder, fileName));
    }

    /**
     * Get the locale specified by the plugin configuration
     *
     * @param configuration The {@link YamlConfiguration} which stores the timezone property
     * @param logger        The plugin {@link Logger} if the configuration is invalid
     * @return The locale that plugin should use
     */
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

    public EventManager getEventManager() {
        return eventManager;
    }

    public Messages getMessages() {
        return messages;
    }
}