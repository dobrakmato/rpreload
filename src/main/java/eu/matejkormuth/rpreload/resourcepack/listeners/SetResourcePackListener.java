package eu.matejkormuth.rpreload.resourcepack.listeners;

import eu.matejkormuth.bmboot.facades.Container;
import eu.matejkormuth.rpreload.resourcepack.ResourcePackModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class SetResourcePackListener implements Listener {

    @EventHandler
    private void onJoinServer(final PlayerJoinEvent event) {
        sendResourcePack(event.getPlayer());
    }

    @EventHandler
    private void onWorldChanged(final PlayerChangedWorldEvent event) {
        sendResourcePack(event.getPlayer());
    }

    /**
     * Sends resource pack to specified player.
     *
     * @param player player to send resource pack to
     */
    private void sendResourcePack(Player player) {
        // Resolve resource pack URL.
        String url = Container
                .get(ResourcePackModule.class)
                .getResolver()
                .process(player);
        // Set resource pack to player.
        player.setResourcePack(url);
    }

}
