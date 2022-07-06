package pl.tuso.essentials.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import pl.tuso.core.util.Color;
import pl.tuso.essentials.XEssentials;

public class JoinQuitMessage implements Listener {
    private final XEssentials xEssentials;

    public JoinQuitMessage(XEssentials xEssentials) {
        this.xEssentials = xEssentials;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        event.joinMessage(this.getFragment(event.getPlayer()).append(Component.text(" joined the game").color(Color.WHITE)));
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        event.quitMessage(this.getFragment(event.getPlayer()).append(Component.text(" left the game").color(Color.WHITE)));
    }

    private @NotNull Component getFragment(@NotNull Player player) {
        User user = this.xEssentials.getLuckPerms().getUserManager().getUser(player.getUniqueId());
        String displayName = PlainTextComponentSerializer.plainText().serialize((player.displayName()));
        String prefix = user.getCachedData().getMetaData().getPrefix() == null ? "" : user.getCachedData().getMetaData().getPrefix();
        String suffix = user.getCachedData().getMetaData().getSuffix() == null ? "" : user.getCachedData().getMetaData().getSuffix();
        return MiniMessage.miniMessage().deserialize(prefix + displayName + suffix);
    }
}
