package pl.tuso.essentials.tablist;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import pl.tuso.essentials.XEssentials;
import pl.tuso.essentials.refresh.Refreshable;

public class TablistContentInjector implements Listener, Refreshable {
    private final XEssentials xEssentials;

    public TablistContentInjector(XEssentials xEssentials) {
        this.xEssentials = xEssentials;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        event.getPlayer().sendPlayerListHeader(this.xEssentials.getConfiguration().getPlayerListHeader());
        event.getPlayer().sendPlayerListFooter(this.xEssentials.getConfiguration().getPlayerListFooter());
    }

    @Override
    public void refresh() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.playerListHeader() == null || player.playerListFooter() == null) {
                player.sendPlayerListHeader(this.xEssentials.getConfiguration().getPlayerListHeader());
                player.sendPlayerListFooter(this.xEssentials.getConfiguration().getPlayerListFooter());
            }
            if (!player.playerListHeader().equals(this.xEssentials.getConfiguration().getPlayerListHeader())) {
                player.sendPlayerListHeader(this.xEssentials.getConfiguration().getPlayerListHeader());
            }
            if (!player.playerListFooter().equals(this.xEssentials.getConfiguration().getPlayerListFooter())) {
                player.sendPlayerListFooter(this.xEssentials.getConfiguration().getPlayerListFooter());
            }
        });
    }
}
