package pl.tuso.essentials.direct;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.tuso.core.lettuce.messaging.Message;
import pl.tuso.core.lettuce.messaging.MessagingListener;

import java.util.UUID;
import java.util.stream.Collectors;

public class DirectMessageListener implements MessagingListener {
    private final DirectMessageManager directMessageManager;
    private final MiniMessage miniMessage;

    public DirectMessageListener(DirectMessageManager directMessageManager) {
        this.directMessageManager = directMessageManager;
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public void action(@NotNull Message message) {
        if (!message.containsParam("uuid") || !message.containsParam("message")) return;
        UUID uuid = UUID.fromString(message.getParam("uuid"));
        if (Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()).contains(uuid)) {
            Player target = Bukkit.getPlayer(uuid);
            this.directMessageManager.playSound(target);
            target.sendMessage(this.miniMessage.deserialize(message.getParam("message")));
        }
    }

    @Override
    public @NotNull String getType() {
        return "DIRECT_MESSAGE";
    }
}
