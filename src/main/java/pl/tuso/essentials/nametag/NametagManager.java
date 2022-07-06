package pl.tuso.essentials.nametag;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class NametagManager implements ManagerProperties { // TODO fix nametags list
    private static final HashMap<UUID, NametagManager> managers = new HashMap<>();
    private final UUID ownerUuid;
    private List<Nametag> nametags;
    private final HashMap<Integer, Nametag> priorities;
    private final List<Player> nearbyPlayers;
    private final Comparator<Nametag> comparator;
    private final double spaceBetweenLines;
    private double height;
    private boolean sneaking;

    public NametagManager(@NotNull Player owner) {
        this.ownerUuid = owner.getUniqueId();
        this.nametags = new ArrayList<>();
        this.priorities = new HashMap<>();
        this.nearbyPlayers = new ArrayList<>();
        this.comparator = new NametagComparator();
        this.spaceBetweenLines = 0.3D;
        this.height = 0.8D;
        this.sneaking = false;

        managers.put(this.ownerUuid, this);
    }

    /** Sets the base height for all lines */
    public void setHeight(double height) {
        this.height = height;
    }

    /** Returns the height location of all lines */
    public Location getHeightLocation() {
        Player owner = Bukkit.getPlayer(this.ownerUuid);
        Location location = owner.getVehicle() == null ? owner.getLocation() : owner.getVehicle().getLocation();
        double y = this.height;
        switch (owner.getPose()) {
            case SNEAKING -> y -= 0.3D;
            case SWIMMING -> y -= 1.2D;
            case SLEEPING -> y -= 1.6D;
            case FALL_FLYING -> y -= 1.2D;
        }
        if (owner.getVehicle() != null) {
            switch (owner.getVehicle().getType()) {
                case BOAT -> y -= 0.45D;
                case PIG -> y += 0.325;
                case HORSE -> y += 0.85;
                case DONKEY -> y += 0.525D;
                case STRIDER -> y += 1.15D;
            }
            if (y == this.height) location.setY(owner.getLocation().getY());
        }
        location.add(0.0D, y, 0.0D);
        return location;
    }

    /** Teleports all lines to the owner for a certain player */
    public void teleport(Player viewer) {
        this.nametags.forEach(nametag -> nametag.teleport(viewer));
    }

    /** Teleports all lines to the owner for all nearby players */
    public void teleport() {
        this.nametags.forEach(nametag -> nametag.teleport());
    }

    /** Returns the list of all nearby players */
    public List<Player> getNearbyPlayers() {
        return this.nearbyPlayers;
    }

    /** Returns true if player is nearby */
    public boolean isNearby(Player viewer) {
        return this.nearbyPlayers.contains(viewer);
    }

    /** Sets shift key down for transparent nametag for all lines */
    public void sneak(boolean sneaking) {
        this.sneaking = sneaking;
        this.nametags.forEach(nametag -> nametag.sneak(sneaking));
    }

    /** Returns the sneak status */
    public boolean isSneaking() {
        return this.sneaking;
    }

    /** Respawns all lines for all nearby players */
    public void respawn() {
        this.nametags.forEach(nametag -> this.nearbyPlayers.forEach(viewer -> nametag.respawn(viewer)));
    }

    /** Spawns all lines for a certain player */
    public void spawn(Player viewer) {
        if (!this.isNearby(viewer)) {
            this.nearbyPlayers.add(viewer);
        }
        this.nametags.forEach(nametag -> nametag.spawn(viewer));
    }

    /** Fixes the heights of the nametags */
    public void fixNametagsHeights() {
        this.nametags.sort(this.comparator);
        this.nametags.forEach(tag -> tag.setOffset(this.nametags.indexOf(tag) * spaceBetweenLines));
    }

    /** Creates the nametag with value and priority */
    public Nametag addNametag(Component value, int priority) {
        Nametag nametag = new Nametag(Bukkit.getPlayer(this.ownerUuid), priority);
        nametag.setValue(value);
        if (this.priorities.get(priority) != null) {
            this.removeNametag(this.priorities.get(priority));
            this.priorities.remove(priority);
        }
        this.priorities.put(priority, nametag);
        this.nametags = priorities.values().stream().collect(Collectors.toList());
        this.fixNametagsHeights();
        this.nearbyPlayers.forEach(viewer -> nametag.spawn(viewer));
        return nametag;
    }

    /** Returns the nametag with the given priority */
    public Nametag getNametag(int priority) {
        Optional<Nametag> nametag = this.nametags.stream().filter(tag -> tag.getPriority() == priority).findAny();
        if (nametag.isEmpty()) return null;
        return nametag.get();
    }

    /** Removes the nametag with the given priority */
    public void removeNametag(int priority) {
        if (!this.hasNametag(priority)) return;
        this.removeNametag(this.getNametag(priority));
    }

    /** Removes the given nametag */
    public void removeNametag(Nametag nametag) {
        if (!this.nametags.contains(nametag)) return;
        this.nametags.remove(nametag);
        this.fixNametagsHeights();
        nametag.destroy();
    }

    /** True if player contains nametag with the given priority */
    public boolean hasNametag(int priority) {
        return this.getNametag(priority) != null;
    }

    /** Removes player from the nearby players list */
    public void unregisterPlayer(Player viewer) {
        if (this.nearbyPlayers.contains(viewer)) this.nearbyPlayers.remove(viewer);
    }

    /** Destroys all lines fot a certain player */
    public void destroy(Player viewer) {
        this.nametags.forEach(nametag -> nametag.destroy(viewer));
        this.unregisterPlayer(viewer);
    }

    /** Destroys all lines for all nearby players and clears the list of nearby players
     * Use this when the player disconnects */
    @Override
    public void destroy() {
        this.nametags.forEach(nametag -> nametag.destroy());
        this.nearbyPlayers.clear();
    }

    /** Refreshes al lines for all nearby players */
    @Override
    public void refresh() {
        this.nametags.forEach(nametag -> nametag.refresh());
    }

    /** Returns the visibility of all nametags */
    @Override
    public boolean getVisibility() {
        Player owner = Bukkit.getPlayer(this.ownerUuid);
        return !(owner.getGameMode() == GameMode.SPECTATOR || owner.hasPotionEffect(PotionEffectType.INVISIBILITY));
    }

    /** Returns the nametag manager of the certain player */
    public static NametagManager get(@NotNull Player owner) {
        return managers.containsKey(owner.getUniqueId()) ? managers.get(owner.getUniqueId()) : new NametagManager(owner);
    }
}
