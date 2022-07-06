package pl.tuso.essentials.packet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PacketPlayerInjector implements Listener {
    private final PacketListener packetListener;

    public PacketPlayerInjector() {
        this.packetListener = new PacketListener();
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        this.packetListener.injectPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        this.packetListener.removePlayer(event.getPlayer());
    }
}
