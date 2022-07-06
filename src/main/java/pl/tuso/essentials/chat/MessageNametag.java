package pl.tuso.essentials.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pl.tuso.essentials.XEssentials;
import pl.tuso.essentials.nametag.Nametag;
import pl.tuso.essentials.nametag.NametagManager;

public class MessageNametag implements Listener {
    private final XEssentials xEssentials;
    private final int DELAY;
    private final TextColor MESSAGE_COLOR;

    public MessageNametag(XEssentials xEssentials) {
        this.xEssentials = xEssentials;
        this.DELAY = 5; // In seconds
        this.MESSAGE_COLOR = TextColor.color(210, 216, 218);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(@NotNull AsyncChatEvent event) {
        NametagManager nametagManager = NametagManager.get(event.getPlayer());
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        if (plainMessage.length() > 16) {
            plainMessage = plainMessage.substring(0, 16) + "...";
        }
        Nametag nametag = nametagManager.addNametag(Component.text(plainMessage).color(this.MESSAGE_COLOR), 1);
        this.removeInAMoment(nametagManager, nametag);
    }

    private void removeInAMoment(NametagManager nametagManager, Nametag nametag) {
        new BukkitRunnable() {
            @Override
            public void run() {
                nametagManager.removeNametag(nametag);
            }
        }.runTaskLater(this.xEssentials, 20 * this.DELAY);
    }
}
