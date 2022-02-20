package fr.flowsqy.customevents.command.subcommand;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.api.Event;
import fr.flowsqy.customevents.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StartSubCommand extends SubCommand {

    private final CustomEventsPlugin plugin;

    public StartSubCommand(CustomEventsPlugin plugin, String name, String permission, String... aliases) {
        super(plugin, name, permission, aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            final String arg = args[1];
            for (Event event : plugin.getCycleManager().getEvents()) {
                if (event.getData().getCommandId().equals(arg)) {
                    plugin.getMessages().sendMessage(sender, "command.start.success", "%event%", arg);
                    event.perform();
                    return true;
                }
            }
            return plugin.getMessages().sendMessage(sender, "command.start.fail", "%event%", arg);
        }
        sender.sendMessage(helpMessage);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
