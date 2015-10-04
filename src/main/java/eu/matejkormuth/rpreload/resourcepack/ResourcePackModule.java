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
package eu.matejkormuth.rpreload.resourcepack;

import eu.matejkormuth.bmboot.Dependency;
import eu.matejkormuth.bmboot.facades.Container;
import eu.matejkormuth.bmboot.internal.Module;
import eu.matejkormuth.rpreload.commands.CommandsModule;
import eu.matejkormuth.rpreload.configuration.ConfigurationsModule;
import eu.matejkormuth.rpreload.resourcepack.commands.RpHelpCommand;
import eu.matejkormuth.rpreload.resourcepack.commands.RpReloadAllCommand;
import eu.matejkormuth.rpreload.resourcepack.commands.RpSetCommand;
import eu.matejkormuth.rpreload.resourcepack.listeners.SetResourcePackListener;
import eu.matejkormuth.rpreload.resourcepack.sources.EmptySettingSource;
import eu.matejkormuth.rpreload.resourcepack.sources.GlobalSettingSource;
import eu.matejkormuth.rpreload.resourcepack.sources.PlayerSettingSource;
import eu.matejkormuth.rpreload.resourcepack.sources.WorldSettingSource;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourcePackModule extends Module {

    private static final Logger log = LoggerFactory.getLogger(ResourcePackModule.class);

    @Dependency
    private ConfigurationsModule configurationsModule;

    @Dependency
    private CommandsModule commandsModule;

    // Mapping: resourcepack_name -> url.
    private YamlConfiguration resourcePacks;

    // Mapping world_name -> resourcepack_name.
    private YamlConfiguration worldPacks;

    // Mapping uuid -> resourcepack_name.
    private YamlConfiguration playerPacks;

    // Resource pack resolver.
    private Chain<Player, String> resolver;

    @Override
    public void onEnable() {
        // Load configurations.
        resourcePacks = configurationsModule.loadOrCreate("resourcepacks", DefaultConfigurations.RESOURCE_PACKS);
        worldPacks = configurationsModule.loadOrCreate("worlds", DefaultConfigurations.WORLD_PACKS);
        playerPacks = configurationsModule.loadOrCreate("players", DefaultConfigurations.PLAYER_PACKS);

        // Check for default empty resource pack.
        if (!resourcePacks.contains("_empty")) {
            log.error("Default '_empty' resource pack was not found in resourcepacks.yml configuration file!");
            log.error("Plugin may not work correctly!");
        }

        // Create applier.
        Container.put(ResourcePackApplier.class, new ResourcePackApplier(this));

        // Check resource packs asynchronously.
        Container.get(BukkitScheduler.class)
                .runTaskAsynchronously(Container.get(Plugin.class), this::checkResourcePacks);

        // Register rp commands.
        commandsModule.command("rp", new RpHelpCommand())
                .subcommand("reloadall", new RpReloadAllCommand())
                .subcommand("set", new RpSetCommand())
                .subcommand("help", new RpHelpCommand());

        // Build resource pack resolver.
        resolver = Chain.first(new PlayerSettingSource())
                .then(new WorldSettingSource())
                .then(new GlobalSettingSource())
                .then(new EmptySettingSource())
                .build();

        // Register listeners.
        listener(new SetResourcePackListener());
    }

    /**
     * Checks availability and size of each resource pack specified in resource
     * pack configuration file.
     */
    private void checkResourcePacks() {
        log.info("Checking resource packs asynchronously...");
        ResourcePackChecker rpc = new ResourcePackChecker();
        resourcePacks.getValues(false)
                .entrySet()
                .stream()
                .map(entry -> rpc.test((String) entry.getValue()))
                .filter(ResourcePackChecker.Status::isError)
                .forEach(status -> log.error("Resource pack {} will be broken on clients! Reason: {}.",
                        status.getUrl(), status.getMessage()));
        log.info("All resource pack were checked!");
    }

    @Override
    public void onDisable() {

    }

    public Chain<Player, String> getResolver() {
        return resolver;
    }

    public YamlConfiguration getResourcePacks() {
        return resourcePacks;
    }

    public YamlConfiguration getWorldPacks() {
        return worldPacks;
    }

    public YamlConfiguration getPlayerPacks() {
        return playerPacks;
    }
}
