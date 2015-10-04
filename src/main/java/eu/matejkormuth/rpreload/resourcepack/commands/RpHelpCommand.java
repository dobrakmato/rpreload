package eu.matejkormuth.rpreload.resourcepack.commands;

import eu.matejkormuth.rpreload.commands.Command;
import eu.matejkormuth.rpreload.commands.CommandArgs;
import org.bukkit.command.CommandSender;

public class RpHelpCommand extends Command {
    @Override
    protected boolean onExecute(CommandSender sender, CommandArgs args) {
        sender.sendMessage("Help!");
        return true;
    }
}
