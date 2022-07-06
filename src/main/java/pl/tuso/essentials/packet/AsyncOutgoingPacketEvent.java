package pl.tuso.essentials.packet;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncOutgoingPacketEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final Player listener;
    private Object packet;
    private boolean cancelled;

    public AsyncOutgoingPacketEvent(Object packet, Player listener) {
        super(true);
        this.listener = listener;
        this.packet = packet;
        this.cancelled = false;
    }

    public Player getListener() {
        return this.listener;
    }

    public Object getPacket() {
        return this.packet;
    }

    public void setPacket(Object packet) {
        this.packet = packet;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
