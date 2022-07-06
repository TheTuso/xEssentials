package pl.tuso.essentials.tablist;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class FakePlayerGenerator {
    private final Property DEFAULT_SKIN;
    private final MinecraftServer minecraftServer;
    private final Collection<ServerPlayer> fakePlayers;
    private final PlayerTeam playerTeam;

    public FakePlayerGenerator() {
        this.DEFAULT_SKIN = new Property("textures", "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=", "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw=");
        this.minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        this.fakePlayers = this.getFakePlayers(80);
        this.playerTeam = new PlayerTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), "9999Fake");
    }

    public void loadFakePlayers(Player receiver) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) receiver).getHandle().connection;
        connection.send(this.createAddPlayersPacket());
        connection.send(this.createAddTeamPacket());
        connection.send(this.createJoinTeamPacket());
    }

    private @NotNull Collection<ServerPlayer> getFakePlayers(int amount) {
        ServerLevel serverLevel = this.minecraftServer.getAllLevels().iterator().next();
        Collection<ServerPlayer> fakePlayers = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            fakePlayers.add(this.createServerPlayer(serverLevel));
        }
        return fakePlayers;
    }

    private @NotNull ServerPlayer createServerPlayer(ServerLevel serverLevel) {
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().put("textures", this.DEFAULT_SKIN);
        ServerPlayer serverPlayer = new ServerPlayer(this.minecraftServer, serverLevel, gameProfile, null);
        CraftPlayer craftPlayer = new CraftPlayer(((CraftServer) Bukkit.getServer()), serverPlayer);
        craftPlayer.playerListName(Component.text("                            ")); // 28 spaces
        craftPlayer.getHandle().latency = 1000;
        craftPlayer.getHandle().gameMode.changeGameModeForPlayer(GameType.SPECTATOR);
        return craftPlayer.getHandle();
    }

    @Contract(" -> new")
    private @NotNull ClientboundPlayerInfoPacket createAddPlayersPacket() {
        return new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, this.fakePlayers);
    }

    @Contract(" -> new")
    private @NotNull ClientboundSetPlayerTeamPacket createAddTeamPacket() {
        return ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(this.playerTeam, true);
    }

    private @NotNull ClientboundSetPlayerTeamPacket createJoinTeamPacket() {
        Collection<String> players = this.fakePlayers.stream().map(serverPlayer -> serverPlayer.getGameProfile().getName()).collect(Collectors.toCollection(ArrayList::new));
        return ClientboundSetPlayerTeamPacket.createMultiplePlayerPacket(this.playerTeam, players, ClientboundSetPlayerTeamPacket.Action.ADD);
    }
}
