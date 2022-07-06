package pl.tuso.essentials.config;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.tuso.core.command.CommandHandler;
import pl.tuso.core.util.Color;
import pl.tuso.essentials.XEssentials;

import java.util.List;

public class ReloadCommand extends CommandHandler {
    private final XEssentials xEssentials;

    public ReloadCommand(XEssentials xEssentials) {
        this.xEssentials = xEssentials;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.xEssentials.getConfiguration().reload();
        sender.sendMessage(Component.text("Configuration reloaded!").color(Color.SHEEN_GREEN));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return ImmutableList.of();
    }
}
