package fr.flowsqy.customevents.command.subcommand;

import fr.flowsqy.customevents.CustomEventsPlugin;
import fr.flowsqy.customevents.api.Event;
import org.bukkit.command.CommandSender;

public class CheckSubCommand extends InteractSubCommand {

    public CheckSubCommand(CustomEventsPlugin plugin, String name, String permission, String... aliases) {
        super(plugin, name, permission, aliases);
    }

    @Override
    protected boolean interact(CommandSender sender, Event event) {
        event.check();
        return true;
    }
}
