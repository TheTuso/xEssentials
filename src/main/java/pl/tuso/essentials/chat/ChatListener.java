package pl.tuso.essentials.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import pl.tuso.essentials.XEssentials;

public class ChatListener implements Listener {
    private final FormatRenderer formatRenderer;

    public ChatListener(XEssentials xEssentials) {
        this.formatRenderer = new FormatRenderer(xEssentials);

        xEssentials.getServer().getPluginManager().registerEvents(new MessageNametag(xEssentials), xEssentials);
        xEssentials.getServer().getPluginManager().registerEvents(new JoinQuitMessage(xEssentials), xEssentials);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(@NotNull AsyncChatEvent event) {
        event.renderer(this.formatRenderer);
    }
}
