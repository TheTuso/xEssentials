package pl.tuso.essentials.chat;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.tuso.core.util.Color;
import pl.tuso.essentials.XEssentials;

import java.util.regex.Pattern;

public class FormatRenderer implements ChatRenderer {
    private final XEssentials xEssentials;
    private final TextColor PING_COLOR;
    private final Sound PING_SOUND;
    private final PlainTextComponentSerializer plainTextComponentSerializer;
    private final MiniMessage miniMessage;

    public FormatRenderer(XEssentials xEssentials) {
        this.xEssentials = xEssentials;
        this.PING_COLOR = TextColor.color(255, 200, 50);
        this.PING_SOUND = Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1, 1);
        this.plainTextComponentSerializer = PlainTextComponentSerializer.plainText();
        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        message = message.color(this.getChatColor(source)); // Applies chat color for the message
        if (viewer instanceof Player tagged && !tagged.getUniqueId().equals(source.getUniqueId()) && this.containsPing(message, tagged)) { // If contains ping
                message = this.getPingFormat(message, tagged);
                tagged.playSound(this.PING_SOUND);
        }
        return this.miniMessage.deserialize(this.getFormat(source)).append(message);
    }

    private @NotNull Component getPingFormat(@NotNull Component message, @NotNull Player tagged) { // Replaces the ping to colored ping
        String displayName = this.plainTextComponentSerializer.serialize(tagged.displayName());
        String pattern = this.plainTextComponentSerializer.serialize(message).toLowerCase().contains("@" + displayName.toLowerCase()) ? "@" + displayName : displayName; // Checks if player has used the '@' symbol
        Component ping = Component.text("@").append(tagged.displayName()).color(this.PING_COLOR); // Colored ping fragment
        return message.replaceText(builder -> builder.match(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE)).replacement(match -> ping));
    }

    private boolean containsPing(Component message, @NotNull Player tagged) { // Checks if component contains ping (display name)
        String plainMessage = this.plainTextComponentSerializer.serialize(message).toLowerCase();
        String plainDisplayName = this.plainTextComponentSerializer.serialize(tagged.displayName()).toLowerCase();
        return plainMessage.matches(".*\\b" + plainDisplayName + "\\b.*");
    }

    private @NotNull String getFormat(@NotNull Player source) { // prefix + display name + suffix + separator | supports miniMessage
        User user = this.xEssentials.getLuckPerms().getUserManager().getUser(source.getUniqueId());
        String displayName = this.plainTextComponentSerializer.serialize(source.displayName());
        String prefix = user.getCachedData().getMetaData().getPrefix() == null ? "" : user.getCachedData().getMetaData().getPrefix();
        String suffix = user.getCachedData().getMetaData().getSuffix() == null ? "" : user.getCachedData().getMetaData().getSuffix();
        String separator = ": ";
        return prefix + displayName + suffix + separator;
    }

    private TextColor getChatColor(@NotNull Player source) { // Returns the chat color meta (white if missing)
        User user = this.xEssentials.getLuckPerms().getUserManager().getUser(source.getUniqueId());
        String hex = user.getCachedData().getMetaData().getMetaValue("chatColor") == null ? "#ffffff" : user.getCachedData().getMetaData().getMetaValue("chatColor");
        TextColor chatColor = TextColor.fromHexString(hex) == null ? Color.WHITE : TextColor.fromHexString(hex);
        return chatColor;
    }
}
