package pl.tuso.essentials.spawn;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;
import pl.tuso.essentials.XEssentials;

public class SpawnOnJoin implements Listener {
    private final XEssentials xEssentials;

    public SpawnOnJoin(XEssentials xEssentials) {
        this.xEssentials = xEssentials;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (this.xEssentials.getConfiguration().shouldSpawnOnJoin()) {
            Location spawn = event.getPlayer().getWorld().getSpawnLocation().add(new Vector(0.5F, 0.0F, 0.5F));
            event.getPlayer().teleport(spawn);
        }
    }
}
