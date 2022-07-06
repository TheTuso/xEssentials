package pl.tuso.essentials.nametag;

import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.tuso.essentials.entity.IntelligentArmorStand;
import pl.tuso.essentials.entity.Registration;

import java.lang.reflect.Field;
import java.util.UUID;

public class Nametag extends IntelligentArmorStand implements NametagProperties {
    private static EntityType NAMETAG;
    private UUID ownerUuid;
    private double offset;
    private int priority;
    private NametagManager nametagManager;

    public Nametag(EntityType<? extends IntelligentArmorStand> type, Level world) {
        super(type, world);
    }

    public Nametag(@NotNull Player owner, int priority) {
        this(NAMETAG, ((CraftWorld) owner.getWorld()).getHandle());
        this.ownerUuid = owner.getUniqueId();
        this.offset = 0.0D;
        this.priority = priority;
        this.nametagManager = NametagManager.get(owner);

        this.setInvisible(true);
        this.setSmall(true);
        this.setNoGravity(true);
        this.getBukkitEntity().customName(Component.text(this.getDisplayName().getString()));
        this.setCustomNameVisible(true);
        this.setPos(owner.getLocation().getX(), nametagManager.getHeightLocation().getY() + offset, owner.getLocation().getZ());
    }

    @Override
    public void setValue(Component value) {
        this.getBukkitEntity().customName(value);
        this.refresh();
    }

    @Override
    public Component getValue() {
        return this.getBukkitEntity().customName();
    }

    /** Sets the priority for the nametag needed to sort all lines */
    @Override
    public void setPriority(int priority) {
        this.priority = priority;
        this.nametagManager.fixNametagsHeights();
    }

    /** Returns the priority of the nametag */
    @Override
    public int getPriority() {
        return this.priority;
    }

    /** Sets the offset for the nametag needed to position the certain line */
    @Override
    public void setOffset(double offset) {
        this.destroy();
        this.offset = offset;
        this.nametagManager.getNearbyPlayers().forEach(viewer -> this.spawn(viewer));
    }

    /** Return the offset of the nametag */
    @Override
    public double getOffset() {
        return this.offset;
    }

    /** Teleports the nametag to the owner for all nearby players */
    @Override
    public void teleport() {
        this.nametagManager.getNearbyPlayers().forEach(viewer -> this.teleport(viewer));
    }

    /** Teleports the nametag to the owner for a certain player */
    @Override
    public void teleport(Player viewer) {
        ((CraftPlayer) viewer).getHandle().connection.send(this.getTeleportEntityPacket());
    }

    /** Sets shift key down for transparent nametag */
    @Override
    public void sneak(boolean sneaking) {
        this.setShiftKeyDown(sneaking);
        this.refresh();
    }

    /** Destroys the nametag for all nearby players */
    @Override
    public void destroy() {
        this.nametagManager.getNearbyPlayers().forEach(viewer -> this.destroy(viewer));
    }

    /** Destroys the nametag for a certain player */
    @Override
    public void destroy(Player viewer) {
        ((CraftPlayer) viewer).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(this.getId()));
    }

    /** Refreshes the nametag for all nearby players */
    @Override
    public void refresh() {
        this.nametagManager.getNearbyPlayers().forEach(viewer -> ((CraftPlayer) viewer).getHandle().connection.send(new ClientboundSetEntityDataPacket(this.getId(), this.getEntityData(), true)));
    }

    /** Spawns the nametag for a certain player */
    @Override
    public void spawn(Player viewer) {
        Location location = this.nametagManager.getHeightLocation();
        this.level = ((CraftWorld) location.getWorld()).getHandle();
        this.setPos(location.getX(), location.getY() + this.offset, location.getZ());
        ((CraftPlayer) viewer).getHandle().connection.send(new ClientboundAddEntityPacket(this));
        this.sneak(Bukkit.getPlayer(this.ownerUuid).isSneaking());
        this.refresh();
    }

    /** Respawns the nametag for a certain player */
    @Override
    public void respawn(Player viewer) {
        this.destroy(viewer);
        this.spawn(viewer);
    }

    /** Returns teleport packet with correct position of the owner */
    private @NotNull ClientboundTeleportEntityPacket getTeleportEntityPacket() {
        Location location = this.nametagManager.getHeightLocation();
        ClientboundTeleportEntityPacket clientboundTeleportEntityPacket = new ClientboundTeleportEntityPacket(this);
        try {
            final Field xField = clientboundTeleportEntityPacket.getClass().getDeclaredField("b");
            final Field yField = clientboundTeleportEntityPacket.getClass().getDeclaredField("c");
            final Field zField = clientboundTeleportEntityPacket.getClass().getDeclaredField("d");
            xField.setAccessible(true);
            yField.setAccessible(true);
            zField.setAccessible(true);
            xField.setDouble(clientboundTeleportEntityPacket, location.getX());
            yField.setDouble(clientboundTeleportEntityPacket, location.getY() + this.offset);
            zField.setDouble(clientboundTeleportEntityPacket, location.getZ());
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
        return clientboundTeleportEntityPacket;
    }

    public static void registerEntityType() {
        NAMETAG = Registration.registerEntity(
                Nametag::new,
                "nametag",
                EntityType.ARMOR_STAND,
                0.0F,
                0.0F
        );
    }
}
