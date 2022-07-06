package pl.tuso.essentials.tablist;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import pl.tuso.essentials.XEssentials;
import pl.tuso.essentials.refresh.Refreshable;

public class TablistPlayerName implements Listener, Refreshable {
    private final XEssentials xEssentials;

    public TablistPlayerName(XEssentials xEssentials) {
        this.xEssentials = xEssentials;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        event.getPlayer().playerListName(this.getFormattedName(event.getPlayer()));
    }

    private @NotNull Component getFormattedName(@NotNull Player owner) {
        User user = this.xEssentials.getLuckPerms().getUserManager().getUser(owner.getUniqueId());
        String displayName = PlainTextComponentSerializer.plainText().serialize(owner.displayName());
        String prefix = user.getCachedData().getMetaData().getPrefix() == null ? "" : user.getCachedData().getMetaData().getPrefix();
        String suffix = user.getCachedData().getMetaData().getSuffix() == null ? "" : user.getCachedData().getMetaData().getSuffix();
        return MiniMessage.miniMessage().deserialize(prefix + displayName + suffix);
    }

    @Override
    public void refresh() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!player.playerListName().equals(TablistPlayerName.this.getFormattedName(player))) {
                player.playerListName(TablistPlayerName.this.getFormattedName(player));
            }
        });
    }
}
