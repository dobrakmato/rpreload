/**
 * rpreload - Resource pack management made easy.
 * Copyright (c) 2015, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
