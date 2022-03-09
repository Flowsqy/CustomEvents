package fr.flowsqy.customevents.command.subcommand;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpSubCommand extends SubCommand {

    private final SubCommand[] subCommands;

    public HelpSubCommand(CustomEventsPlugin plugin, String name, String permission, SubCommand[] subCommands, String... aliases) {
        super(plugin, name, permission, aliases);
        this.subCommands = subCommands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getHelpMessage() == null) {
                continue;
            }
            if (sender.hasPermission(subCommand.getPermission())) {
                sender.sendMessage(subCommand.getHelpMessage());
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
