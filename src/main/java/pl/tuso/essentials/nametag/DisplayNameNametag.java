package pl.tuso.essentials.nametag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pl.tuso.essentials.XEssentials;

public class DisplayNameNametag implements Listener {
    private final XEssentials xEssentials;

    public DisplayNameNametag(XEssentials xEssentials) {
        this.xEssentials = xEssentials;
        this.startRefresher();
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        NametagManager nametagManager = NametagManager.get(event.getPlayer());
        if (!nametagManager.hasNametag(0)) {
            nametagManager.addNametag(this.getDefaultFormat(event.getPlayer()), 0);
        }
    }

    private void startRefresher() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    NametagManager nametagManager = NametagManager.get(player);
                    if (nametagManager.hasNametag(0) && !(nametagManager.getNametag(0).getValue().equals(DisplayNameNametag.this.getDefaultFormat(player)))) {
                        nametagManager.getNametag(0).setValue(DisplayNameNametag.this.getDefaultFormat(player));
                    }
                });
            }
        }.runTaskTimer(this.xEssentials, 0, 20 * 3);
    }

    private @NotNull Component getDefaultFormat(@NotNull Player owner) {
        User user = this.xEssentials.getLuckPerms().getUserManager().getUser(owner.getUniqueId());
        String displayName = PlainTextComponentSerializer.plainText().serialize(owner.displayName());
        String prefix = user.getCachedData().getMetaData().getPrefix() == null ? "" : user.getCachedData().getMetaData().getPrefix();
        String suffix = user.getCachedData().getMetaData().getSuffix() == null ? "" : user.getCachedData().getMetaData().getSuffix();
        return MiniMessage.miniMessage().deserialize(prefix + displayName + suffix);
    }
}
