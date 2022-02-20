package fr.flowsqy.customevents.command;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.command.subcommand.HelpSubCommand;
import fr.flowsqy.customevents.command.subcommand.InfoSubCommand;
import fr.flowsqy.customevents.command.subcommand.ReloadSubCommand;
import fr.flowsqy.customevents.command.subcommand.StartSubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CustomEventsCommand implements TabExecutor {

    private final SubCommand[] subCommands;

    public CustomEventsCommand(CustomEventsPlugin plugin) {
        subCommands = new SubCommand[4];
        initSubCommands(plugin);
    }

    private void initSubCommands(CustomEventsPlugin plugin) {
        subCommands[0] = new HelpSubCommand(
                plugin,
                "help",
                "customevents.command.help",
                subCommands,
                "h"
        );
        subCommands[1] = new InfoSubCommand(
                plugin,
                "info",
                "customevents.command.info",
                "i"
        );
        subCommands[2] = new StartSubCommand(
                plugin,
                "start",
                "customevents.command.start",
                "s"
        );
        subCommands[3] = new ReloadSubCommand(
                plugin,
                "reload",
                "customevents.command.reload",
                "rl"
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            final String arg = args[0].toLowerCase(Locale.ROOT);
            final SubCommand subCommand = getSubCommand(arg);
            if (subCommand != null && sender.hasPermission(subCommand.getPermission())) {
                return subCommand.onCommand(sender, command, label, args);
            }
        }
        final SubCommand helpSubCommand = subCommands[0];
        if (sender.hasPermission(helpSubCommand.getPermission())) {
            return helpSubCommand.onCommand(sender, command, label, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            final String arg = args[0].toLowerCase(Locale.ROOT);
            final SubCommand subCommand = getSubCommand(arg);
            if (subCommand != null && sender.hasPermission(subCommand.getPermission())) {
                return subCommand.onTabComplete(sender, command, alias, args);
            }
            return Collections.emptyList();
        }

        if (args.length == 1) {
            final String arg = args[0].toLowerCase(Locale.ROOT);
            final List<String> completions = new LinkedList<>();
            for (SubCommand subCmd : subCommands) {
                if (subCmd.getName().startsWith(arg) && sender.hasPermission(subCmd.getPermission())) {
                    completions.add(subCmd.getName());
                }
            }
            return completions;
        }

        final List<String> completions = new LinkedList<>();
        for (SubCommand subCommand : subCommands) {
            if (sender.hasPermission(subCommand.getPermission())) {
                completions.add(subCommand.getName());
            }
        }

        return completions;
    }

    private SubCommand getSubCommand(String arg) {
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equals(arg) || subCommand.getAliases().contains(arg)) {
                return subCommand;
            }
        }
        return null;
    }

}
