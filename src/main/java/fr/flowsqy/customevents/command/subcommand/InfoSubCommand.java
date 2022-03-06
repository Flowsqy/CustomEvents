package fr.flowsqy.customevents.command.subcommand;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.api.Event;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class InfoSubCommand extends InteractSubCommand {

    public InfoSubCommand(CustomEventsPlugin plugin, String name, String permission, String... aliases) {
        super(plugin, name, permission, aliases);
    }

    @Override
    protected boolean interact(CommandSender sender, Event event) {
        for (String line : event.getData().getInfo()) {
            if (line == null) {
                continue;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
        return true;
    }
}
