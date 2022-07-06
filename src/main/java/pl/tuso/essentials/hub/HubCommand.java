package pl.tuso.essentials.hub;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.tuso.core.command.CommandHandler;
import pl.tuso.core.connection.ServerConnection;
import pl.tuso.core.util.Color;
import pl.tuso.essentials.XEssentials;

import java.util.List;

public class HubCommand extends CommandHandler {
    private final XEssentials xEssentials;
    private final ServerConnection serverConnection;

    public HubCommand(@NotNull XEssentials xEssentials) {
        this.xEssentials = xEssentials;
        this.serverConnection = new ServerConnection(xEssentials.getCore());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player player) {
            if (args.length != 0) {
                player.sendMessage(Component.text("This command doesn't exist, or does it?", Color.MAXIMUM_RED));
                return false;
            }
            if (this.xEssentials.getCore().getServerInfo().getName().contains("hub")) {
                Location spawn = player.getWorld().getSpawnLocation().add(new Vector(0.5F, 0.0F, 0.5F));
                player.teleport(spawn);
            } else {
                this.serverConnection.connect(player, "hub");
            }
            player.sendMessage(Component.text("Travelling to hub!", Color.ORANGE_PEEL));
            return true;
        } else {
            sender.sendMessage(Component.text("Only player can use this command!", Color.MAXIMUM_RED));
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return ImmutableList.of();
    }
}
