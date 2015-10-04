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

import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Applies resource pack to player.
 */
public class ResourcePackApplier {

    private static final Logger log = LoggerFactory.getLogger(ResourcePackApplier.class);

    // Player last resource packs.
    private final Map<UUID, String> lastResourcePacks = new HashMap<>();

    private final ResourcePackModule rpm;

    public ResourcePackApplier(ResourcePackModule resourcePackModule) {
        this.rpm = resourcePackModule;
    }

    public void apply(@Nonnull Player player, boolean force) {
        String url = rpm.getResolver().process(player);
        apply(player, url, force);
    }

    private void apply(@Nonnull Player player, @Nonnull String url, boolean force) {
        // Null check hidden here.
        if (force || !url.equals(lastResourcePacks.get(player.getUniqueId()))) {
            log.info("Sending resource pack {} to player {}.", url, player.getName());
            player.setResourcePack(url);
            // Update last resource pack.
            lastResourcePacks.put(player.getUniqueId(), url);
        }
    }

    public void clean(Player player) {
        lastResourcePacks.remove(player.getUniqueId());
    }
}
