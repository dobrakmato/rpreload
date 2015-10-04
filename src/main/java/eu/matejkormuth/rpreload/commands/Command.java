package eu.matejkormuth.rpreload.commands;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Represents executable chat command.
 */
public abstract class Command {
    // All subcommands.
    private final Map<String, Command> subcommands = new HashMap<>(8);
    // Parent command.
    private Command parent = null;

    /**
     * Executes this command as specified sender with specified arguments.
     *
     * @param sender sender of this command
     * @param args   argument of this command
     * @return true if command is successful, false otherwise
     */
    public boolean execute(CommandSender sender, CommandArgs args) {
        // If we got any arguments.
        if (args.length() > 0) {
            // Check if next argument is
            if (subcommands.containsKey(args.peekNext().toLowerCase(Locale.ENGLISH))) {
                return subcommands.get(args.next()).execute(sender, args);
            }
        }
        // Else.
        return onExecute(sender, args);
    }

    protected abstract boolean onExecute(CommandSender sender, CommandArgs args);

    /**
     * Registers sub-command of this command with specified name and command executor.
     *
     * @param name     name of this subcommand
     * @param executor executor of this subcommand
     * @return this command
     */
    public Command subcommand(String name, Command executor) {
        this.subcommands.put(name.toLowerCase(Locale.ENGLISH), executor);

        return this;
    }

    /**
     * Returns parent of this command, or null.
     *
     * @return parent command of this command or null
     */
    public Command parent() {
        return parent;
    }
}
