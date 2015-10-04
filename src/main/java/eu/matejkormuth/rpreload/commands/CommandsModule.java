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

import eu.matejkormuth.bmboot.facades.Container;
import eu.matejkormuth.bmboot.internal.Module;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class CommandsModule extends Module {

    private static final Logger log = LoggerFactory.getLogger(CommandsModule.class);

    private CommandMap commandMap;

    @Override
    public void onEnable() {
        // Get command map from server.
        acquireCommandMap();
    }

    /**
     * Sets command map variable using reflection from bukkit internals.
     */
    private void acquireCommandMap() {
        try {
            PluginManager pm = Container.get(Server.class).getPluginManager();

            Class<SimplePluginManager> spmClass = SimplePluginManager.class;
            Field scmField = spmClass.getDeclaredField("commandMap");

            // Retrieve value.
            Object val = scmField.get(pm);

            this.commandMap = (CommandMap) val;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Can't retrieve Bukkit commandMap using reflection! Commands may not work!");
        }
    }

    @Override
    public void onDisable() {

    }

    /**
     * Registers specified executor as command with specified name on this server.
     *
     * @param command  name of command
     * @param executor command executor
     * @return passed command executor for method chaining
     */
    public Command command(String command, Command executor) {
        // Register command.
        commandMap.register(command, "bm", new CommandAdapter(command, executor));
        return executor;
    }

    /**
     * Provides adapter between bukkit command class and out command class.
     */
    private static class CommandAdapter extends org.bukkit.command.Command {
        private final Command executor;

        public CommandAdapter(String command, Command executor) {
            super(command);
            this.executor = executor;
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return executor.execute(sender, new CommandArgs(args));
        }
    }
}
