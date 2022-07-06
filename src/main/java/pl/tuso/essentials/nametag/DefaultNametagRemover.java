package pl.tuso.essentials.nametag;

import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import pl.tuso.essentials.packet.AsyncOutgoingPacketEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class DefaultNametagRemover implements Listener {
    private final PlayerTeam playerTeam;

    public DefaultNametagRemover() {
        this.playerTeam = new PlayerTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), "nametag");
        this.playerTeam.setNameTagVisibility(Team.Visibility.NEVER);
        this.playerTeam.setSeeFriendlyInvisibles(false);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        ((CraftPlayer) event.getPlayer()).getHandle().connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(this.playerTeam, true));
        Collection<String> players = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toCollection(ArrayList::new));
        Bukkit.getOnlinePlayers().forEach(player -> {
            ((CraftPlayer) player).getHandle().connection.send(
                    ClientboundSetPlayerTeamPacket.createMultiplePlayerPacket(
                            this.playerTeam,
                            players,
                            ClientboundSetPlayerTeamPacket.Action.ADD
                    )
            );
        });
    }

    @TestOnly
    @EventHandler
    public void onPacket(@NotNull AsyncOutgoingPacketEvent event) {
        if (event.getPacket() instanceof ClientboundSetPlayerTeamPacket packet) {
            if (packet.getName().equals(this.playerTeam.getName())) return;
            event.setCancelled(true);
        }
    }
}
