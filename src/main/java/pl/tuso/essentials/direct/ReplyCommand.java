package pl.tuso.essentials.direct;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.tuso.core.command.CommandHandler;
import pl.tuso.core.util.Color;

import java.util.List;

public class ReplyCommand extends CommandHandler {
    private final DirectMessageManager directMessageManager;
    private final StringBuilder stringBuilder;

    public ReplyCommand(DirectMessageManager directMessageManager) {
        this.directMessageManager = directMessageManager;
        this.stringBuilder = new StringBuilder();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: ", Color.MAXIMUM_RED).append(Component.text("/" + command.getLabel() + " <message>", Color.WHITE)));
            return false;
        }
        String senderUsername = sender instanceof Player player ? player.getName() : "Secret";
        if (!this.directMessageManager.hasConversation(senderUsername)) {
            sender.sendMessage(Component.text("There's not anyone to reply to, no one has sent you a message yet!", Color.MAXIMUM_RED));
            return false;
        }
        if (!this.directMessageManager.isOnline(this.directMessageManager.getConversationer(senderUsername))) {
            sender.sendMessage(Component.text(this.directMessageManager.getConversationer(senderUsername) + " is not online!", Color.MAXIMUM_RED));
            this.directMessageManager.removeConversation(senderUsername);
            return false;
        }
        String message = "";
        for (int i = 0; i < args.length; i++) {
            message = this.stringBuilder.append(args[i] + " ").toString();
        }
        this.stringBuilder.setLength(0); // Clears the string builder
        this.directMessageManager.sendDirectMessage(sender, this.directMessageManager.getConversationer(senderUsername), message);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return ImmutableList.of();
    }
}
