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
import java.util.stream.Collectors;

public class DirectMessageCommand extends CommandHandler { // TODO do it lol
    private final DirectMessageManager directMessageManager;
    private final StringBuilder stringBuilder;

    public DirectMessageCommand(DirectMessageManager directMessageManager) {
        this.directMessageManager = directMessageManager;
        this.stringBuilder = new StringBuilder();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length <= 1) {
            sender.sendMessage(Component.text("Usage: ", Color.MAXIMUM_RED).append(Component.text("/" + command.getLabel() + " <player> <message>", Color.WHITE)));
            return false;
        }
        String targetUsername = args[0];
        String message = "";
        if (!this.directMessageManager.isOnline(targetUsername)) {
            sender.sendMessage(Component.text(targetUsername + " is not online!", Color.MAXIMUM_RED));
            return false;
        }
        for (int i = 1; i < args.length; i++) {
            message = this.stringBuilder.append(args[i] + " ").toString();
        }
        this.stringBuilder.setLength(0); // Clears the string builder
        return this.directMessageManager.sendDirectMessage(sender, targetUsername, message);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            List<String> suggestions = this.directMessageManager.getPlayers().stream().collect(Collectors.toList());
            if (sender instanceof Player player) { // Removes sender from the suggestions list
                suggestions.remove(player.getName());
            }
            return suggestions.stream().filter(name -> name.regionMatches(true, 0, args[0], 0, args[0].length()))
                    .collect(Collectors.toList());
        }
        return ImmutableList.of();
    }
}
