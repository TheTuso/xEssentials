package pl.tuso.essentials.packet;

import io.netty.channel.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketListener {
    public void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }

    public void injectPlayer(@NotNull Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                AsyncIncomingPacketEvent packetEvent = new AsyncIncomingPacketEvent(packet, player);
                Bukkit.getPluginManager().callEvent(packetEvent);
                if (packetEvent.isCancelled()) return;
                super.channelRead(channelHandlerContext, packetEvent.getPacket());
            }
            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                AsyncOutgoingPacketEvent packetEvent = new AsyncOutgoingPacketEvent(packet, player);
                Bukkit.getPluginManager().callEvent(packetEvent);
                if (packetEvent.isCancelled()) return;
                super.write(channelHandlerContext, packetEvent.getPacket(), channelPromise);
            }
        };
        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().connection.connection.channel.pipeline();
        if (pipeline.context(player.getName()) == null) {
            pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
        }
    }
}
