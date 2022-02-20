package fr.flowsqy.customevents.command.subcommand;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadSubCommand extends SubCommand {

    private final CustomEventsPlugin plugin;

    public ReloadSubCommand(CustomEventsPlugin plugin, String name, String permission, String... aliases) {
        super(plugin, name, permission, aliases);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.getCycleManager().reload();
        plugin.getMessages().sendMessage(sender, "command.reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
