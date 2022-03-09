package fr.flowsqy.customevents.command.subcommand;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class InteractSubCommand extends SubCommand {

    private final CustomEventsPlugin plugin;

    public InteractSubCommand(CustomEventsPlugin plugin, String name, String permission, String... aliases) {
        super(plugin, name, permission, aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            final String arg = args[1];
            for (Event event : plugin.getCycleManager().getEvents()) {
                if (event.getData().getCommandId().equals(arg)) {
                    plugin.getMessages().sendMessage(sender, "command." + name + ".success", "%event%", arg);
                    return interact(sender, event);
                }
            }
            return plugin.getMessages().sendMessage(sender, "command." + name + ".fail", "%event%", arg);
        }
        if (helpMessage != null) {
            sender.sendMessage(helpMessage);
        }
        return true;
    }

    protected abstract boolean interact(CommandSender sender, Event event);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            final String arg = args[1].toLowerCase(Locale.ROOT);
            final Stream<String> eventsStream = plugin
                    .getCycleManager()
                    .getEvents()
                    .stream()
                    .map(event -> event.getData().getCommandId());
            if (arg.isEmpty()) {
                return eventsStream.collect(Collectors.toList());
            }
            return eventsStream
                    .filter(event -> event.startsWith(arg))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
