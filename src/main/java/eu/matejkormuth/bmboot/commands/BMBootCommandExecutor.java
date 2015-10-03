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
package eu.matejkormuth.bmboot.commands;

import eu.matejkormuth.bmboot.internal.BukkitPlugin;
import eu.matejkormuth.bmboot.internal.Module;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Manifest;

public class BMBootCommandExecutor implements CommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(BMBootCommandExecutor.class);
    private final BukkitPlugin plugin;

    public BMBootCommandExecutor(BukkitPlugin bukkitPlugin) {
        this.plugin = bukkitPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("bmboot") || sender.isOp()) {
            // Find version information.
            URLClassLoader cl = ((URLClassLoader) this.getClass().getClassLoader());
            String title = "unknown";
            String version = "unknown";
            String buildNumber = "unknown";
            String scmRevision = "unknown";
            try {
                URL url = cl.findResource("META-INF/MANIFEST.MF");
                Manifest manifest = new Manifest(url.openStream());
                // do stuff with it
                title = manifest.getMainAttributes().getValue("Implementation-Title");
                version = manifest.getMainAttributes().getValue("Implementation-Version");
                buildNumber = manifest.getMainAttributes().getValue("Implementation-Build-Number");
                scmRevision = manifest.getMainAttributes().getValue("Implementation-SCM-Revision");
            } catch (IOException e) {
                log.error("Error while displaying information.", e);
            }

            sender.sendMessage(ChatColor.YELLOW + plugin.getBootstrapName() + " " + plugin.getBootstrapVersion());
            sender.sendMessage(ChatColor.BLUE + "Mod: " + title + " " + version);
            sender.sendMessage(ChatColor.GRAY + "Rev: " + scmRevision);
            sender.sendMessage(ChatColor.GRAY + "Build number: " + buildNumber);

            StringBuilder modules = new StringBuilder();
            int disabledModules = 0;
            for (Module m : plugin.getAllModules()) {
                if (m.isEnabled()) {
                    modules.append(ChatColor.GREEN + m.toString().replace("Module", ""));
                } else {
                    disabledModules++;
                    modules.append(ChatColor.RED + m.toString().replace("Module", ""));
                }
                modules.append(ChatColor.WHITE + ", ");
            }

            String modulesStr = modules.toString();

            sender.sendMessage(ChatColor.YELLOW + "Modules (" + plugin.getAllModules().size() + "): "
                    + modulesStr.substring(0, modulesStr.length() - 2));

            if (disabledModules > 0) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE +
                        "Please check logs to see more information about why there are "
                        + disabledModules + " disabled modules!");
            }

            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
            return true;
        }
    }
}
