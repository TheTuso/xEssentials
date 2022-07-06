package pl.tuso.essentials.team;

import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import pl.tuso.essentials.packet.AsyncOutgoingPacketEvent;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TeamPacketFixer implements Listener {
    @EventHandler
    public void onPacket(@NotNull AsyncOutgoingPacketEvent event) {
        if (event.getPacket() instanceof ClientboundSetPlayerTeamPacket packet) {
            ArrayList<String> teams = Bukkit.getScoreboardManager().getMainScoreboard().getTeams()
                    .stream().map(team -> team.getName()).collect(Collectors.toCollection(ArrayList::new));
            if (teams.contains(packet.getName())) event.setCancelled(true);
        }
    }
}
