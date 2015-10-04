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
package eu.matejkormuth.rpreload.resourcepack.commands;

import eu.matejkormuth.bmboot.facades.Container;
import eu.matejkormuth.rpreload.commands.Command;
import eu.matejkormuth.rpreload.commands.CommandArgs;
import eu.matejkormuth.rpreload.configuration.ConfigurationsModule;
import eu.matejkormuth.rpreload.resourcepack.ResourcePackApplier;
import eu.matejkormuth.rpreload.resourcepack.ResourcePackModule;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RpSetCommand extends Command {
    @Override
    protected boolean onExecute(CommandSender sender, CommandArgs args) {
        if (sender instanceof Player) {
            String rpName = args.next();
            ResourcePackModule rpm = Container.get(ResourcePackModule.class);
            // Check if name is valid.
            if (rpm.getResourcePacks().contains(rpName)) {
                // Set preferred resource pack.
                Container.get(ResourcePackModule.class)
                        .getPlayerPacks()
                        .set(((Player) sender).getUniqueId().toString(), rpName);
                // Save players configuration.
                Container.get(ConfigurationsModule.class).save("players", Container.get(ResourcePackModule.class)
                        .getPlayerPacks());
                // Set desired resource pack in this session.
                Container.get(ResourcePackApplier.class).apply((Player) sender, true);
                sender.sendMessage(ChatColor.GREEN + "Resource pack set.");
            } else {
                sender.sendMessage(ChatColor.RED + String.format("Specified resource pack ('%s') was not found!",
                        rpName));
            }
        }
        return true;
    }
}
