package pl.tuso.essentials.direct;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.tuso.core.lettuce.messaging.Message;
import pl.tuso.core.util.Color;
import pl.tuso.essentials.XEssentials;

import java.util.Collection;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DirectMessageManager {
    private final XEssentials xEssentials;
    private final TextColor MESSAGE_COLOR;
    private final Component YOU;
    private final MiniMessage miniMessage;
    private final Sound MESSAGE_SOUND;

    public DirectMessageManager(@NotNull XEssentials xEssentials) {
        this.xEssentials = xEssentials;
        this.MESSAGE_COLOR = TextColor.color(210, 216, 218);
        this.YOU = Component.text("You", Color.ORANGE_PEEL);
        this.miniMessage = MiniMessage.miniMessage();
        this.MESSAGE_SOUND = Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1, 1);

        this.xEssentials.getCore().getMessagingService().registerListener(new DirectMessageListener(this));
        new DirectMessageCommand(this).register(xEssentials.getCommand("msg"));
        new ReplyCommand(this).register(xEssentials.getCommand("reply"));
    }

    public Collection<String> getPlayers() {
        return this.xEssentials.getProxyInfo().getAllPlayers();
    }

    public boolean isOnline(@NotNull String username) {
        return this.getCaseSensitiveUsername(username) != null;
    }

    private @Nullable String getCaseSensitiveUsername(String username) {
        for (String name : this.getPlayers()) {
            if (name.equalsIgnoreCase(username)) return name;
        }
        return null;
    }

    public boolean sendDirectMessage(@NotNull CommandSender sender, String targetUsername, String message) {
        String senderUsername = sender instanceof Player player ? player.getName() : "Secret";
        if (senderUsername.equalsIgnoreCase(targetUsername)) {
            sender.sendMessage(Component.text("You cannot message yourself silly!", Color.MAXIMUM_RED));
            return false;
        }
        String caseSensitiveUsername = this.getCaseSensitiveUsername(targetUsername);
        if (this.hasConversation(senderUsername)) this.removeConversation(senderUsername);
        this.setAndRemoveAfter(caseSensitiveUsername, senderUsername, 32);
        Component format = this.format(senderUsername, caseSensitiveUsername, message);
        sender.sendMessage(format.replaceText(builder -> builder.match(Pattern.compile(senderUsername, Pattern.CASE_INSENSITIVE)).replacement(this.YOU)));
        if (Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()).contains(caseSensitiveUsername)) {
            Player target = Bukkit.getPlayer(caseSensitiveUsername);
            this.playSound(target);
            target.sendMessage(format.replaceText(builder -> builder.match(Pattern.compile(caseSensitiveUsername, Pattern.CASE_INSENSITIVE)).replacement(this.YOU)));
            return true;
        } else {
            return this.sendToOtherServer(this.xEssentials.getProxyInfo().getUuid(caseSensitiveUsername), format.replaceText(builder -> builder.match(Pattern.compile(caseSensitiveUsername, Pattern.CASE_INSENSITIVE)).replacement(this.YOU)));
        }
    }

    public void playSound(@NotNull Player target) {
        target.playSound(this.MESSAGE_SOUND);
    }

    private boolean sendToOtherServer(@NotNull UUID uuid, Component message) {
        this.xEssentials.getCore().getMessagingService().sendOutgoingMessage(
                new Message("DIRECT_MESSAGE")
                        .setParam("uuid", uuid.toString())
                        .setParam("message", this.miniMessage.serialize(message))
        );
        return true;
    }

    private @NotNull Component format(String sender, String target, String message) {
        return Component.text(sender + " → " + target + ": ", Color.ORANGE_PEEL).append(Component.text(message, this.MESSAGE_COLOR));
    }

    private void setAndRemoveAfter(String key, String value, int seconds) {
        this.xEssentials.getCore().getRedisWrapper().set(key, value);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!DirectMessageManager.this.hasConversation(key)) return;
                DirectMessageManager.this.xEssentials.getCore().getRedisWrapper().remove(key);
            }
        }.runTaskLater(this.xEssentials, 20 * seconds);
    }

    public boolean hasConversation(String player) {
        return this.xEssentials.getCore().getRedisWrapper().get(player) != null;
    }

    public String getConversationer(String player) {
        if (!this.hasConversation(player)) return null;
        return this.xEssentials.getCore().getRedisWrapper().get(player);
    }

    public void removeConversation(String username) {
        this.xEssentials.getCore().getRedisWrapper().remove(username);
    }
}
