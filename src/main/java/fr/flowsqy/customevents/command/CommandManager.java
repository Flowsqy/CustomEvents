package fr.flowsqy.customevents.command;

import fr.flowsqy.customevents.CustomEventsPlugin;
import org.bukkit.command.PluginCommand;

import java.util.Objects;

public class CommandManager {

    public CommandManager(CustomEventsPlugin plugin) {
        final PluginCommand customEventsCommand = plugin.getCommand("customevents");
        Objects.requireNonNull(customEventsCommand);
        final CustomEventsCommand customEventsExecutor = new CustomEventsCommand();
        customEventsCommand.setExecutor(customEventsExecutor);
        customEventsCommand.setTabCompleter(customEventsExecutor);
        customEventsCommand.setPermissionMessage(plugin.getMessages().getMessage("command.no-perm"));
    }

}
