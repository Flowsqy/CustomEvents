package fr.flowsqy.customevents.command;

import fr.flowsqy.customevents.CustomEventsPlugin;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class SubCommand implements TabExecutor {

    protected final String name;
    protected final String permission;
    protected final String helpMessage;
    protected final Set<String> aliases;

    public SubCommand(CustomEventsPlugin plugin, String name, String permission, String... aliases) {
        this(
                name,
                permission,
                plugin.getMessages().getMessage("command.help." + name),
                aliases == null ? Collections.emptySet() : new HashSet<>(Arrays.asList(aliases))
        );
    }

    public SubCommand(String name, String permission, String helpMessage, Set<String> aliases) {
        this.name = name;
        this.permission = permission;
        this.helpMessage = helpMessage;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public Set<String> getAliases() {
        return aliases;
    }
}
