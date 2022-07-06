package pl.tuso.essentials.nametag;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pl.tuso.essentials.XEssentials;

import java.util.ArrayList;
import java.util.List;

public class NametagBehavior implements Listener {
    private final XEssentials xEssentials;
    private final double renderDistance;

    public NametagBehavior(@NotNull XEssentials xEssentials) {
        this.xEssentials = xEssentials;
        this.renderDistance = 32.0D;

        //xEssentials.getServer().getPluginManager().registerEvents(new DefaultNametagRemover(), xEssentials); // Currently disabled
        xEssentials.getServer().getPluginManager().registerEvents(new DisplayNameNametag(xEssentials), xEssentials);

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    NametagManager nametagManager = getNametagManager(player);
                    nametagManager.teleport();
                });
            }
        }.runTaskTimer(xEssentials, 0, 0);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Bukkit.getOnlinePlayers().forEach(viewer -> {
                this.spawnNametagsSafety(viewer, event.getPlayer());
                this.spawnNametagsSafety(event.getPlayer(), viewer);
        });
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Bukkit.getOnlinePlayers().forEach(viewer -> this.getNametagManager(viewer).unregisterPlayer(event.getPlayer()));
        this.getNametagManager(event.getPlayer()).destroy();
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Bukkit.getOnlinePlayers().forEach(viewer -> {
            if (viewer.getWorld().equals(event.getFrom())) {
                this.getNametagManager(event.getPlayer()).destroy(viewer);
            } else {
                this.spawnNametagsSafety(viewer, event.getPlayer());
                this.spawnNametagsSafety(event.getPlayer(), viewer);
            }
        });
    }

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        Bukkit.getOnlinePlayers().forEach(viewer -> {
            double distance = this.getDistance(event.getPlayer(), viewer);
            if (distance <= this.renderDistance) {
                if (!this.getNametagManager(event.getPlayer()).isNearby(viewer)) this.spawnNametagsSafety(viewer, event.getPlayer());
                if (!this.getNametagManager(viewer).isNearby(event.getPlayer())) this.spawnNametagsSafety(event.getPlayer(), viewer);
            } else {
                if (this.getNametagManager(event.getPlayer()).isNearby(viewer)) this.getNametagManager(event.getPlayer()).destroy(viewer);
                if (this.getNametagManager(viewer).isNearby(event.getPlayer())) this.getNametagManager(viewer).destroy(event.getPlayer());
            }
        });
    }

    @EventHandler
    public void onToogleSneak(@NotNull PlayerToggleSneakEvent event) {
        NametagManager nametagManager = this.getNametagManager(event.getPlayer());
        nametagManager.sneak(!event.getPlayer().isSneaking());
    }

    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent event) {
        this.getNametagManager(event.getPlayer()).destroy();
        Bukkit.getOnlinePlayers().forEach(other -> this.getNametagManager(other).destroy(event.getPlayer()));
    }

    @EventHandler
    public void onGamemodeChange(@NotNull PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() == GameMode.SPECTATOR) {
            List<Player> toRemove = new ArrayList<>();
            this.getNametagManager(event.getPlayer()).getNearbyPlayers().forEach(viewer -> {
                if (viewer.getGameMode() == GameMode.SPECTATOR) {
                    this.spawnNametags(event.getPlayer(), viewer);
                } else {
                    toRemove.add(viewer);
                }
            });
            toRemove.forEach(viewer -> this.getNametagManager(event.getPlayer()).destroy(viewer));
            return;
        }
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            Bukkit.getOnlinePlayers().forEach(viewer -> {
                if (viewer.getGameMode() == GameMode.SPECTATOR) {
                    this.getNametagManager(viewer).destroy(event.getPlayer());
                } else {
                    this.spawnNametags(viewer, event.getPlayer());
                }
            });
            return;
        }
    }

    @EventHandler
    public void onEffect(@NotNull EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getAction() == EntityPotionEffectEvent.Action.ADDED && event.getModifiedType().equals(PotionEffectType.INVISIBILITY)) {
                this.getNametagManager(player).destroy();
                return;
            }
            if ((event.getAction() == EntityPotionEffectEvent.Action.CLEARED ||
                    event.getAction() == EntityPotionEffectEvent.Action.REMOVED) &&
                    event.getModifiedType().equals(PotionEffectType.INVISIBILITY)) {
                Bukkit.getOnlinePlayers().forEach(viewer -> this.spawnNametags(viewer, player));
                return;
            }
        }
    }

    private void spawnNametagsSafety(@NotNull Player viewer, @NotNull Player target) {
        if (!this.getNametagManager(target).getVisibility()) return;
        this.spawnNametags(viewer, target);
    }

    private void spawnNametags(@NotNull Player viewer, @NotNull Player target) {
        if (target.equals(viewer)) return;
        if (!target.isValid() || !viewer.isValid()) return;
        if (!viewer.getWorld().equals(target.getWorld())) return;
        if (this.getDistance(viewer, target) <= this.renderDistance && viewer.canSee(target)) { // TODO vanish
            this.getNametagManager(target).spawn(viewer);
        }
    }

    private double getDistance(@NotNull Player from, @NotNull Player to) {
        Location fromLocation = from.getLocation();
        Location toLocation = to.getLocation();
        return Math.sqrt(Math.pow(fromLocation.getX() - toLocation.getX(), 2) + Math.pow(fromLocation.getZ() - toLocation.getZ(), 2));
    }

    public NametagManager getNametagManager(Player owner) {
        return NametagManager.get(owner);
    }
}
