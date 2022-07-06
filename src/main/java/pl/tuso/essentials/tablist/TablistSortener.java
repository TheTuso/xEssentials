package pl.tuso.essentials.tablist;

import net.luckperms.api.model.group.Group;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.tuso.essentials.XEssentials;
import pl.tuso.essentials.refresh.Refreshable;

import java.util.*;
import java.util.stream.Collectors;

public class TablistSortener implements Listener, Refreshable { // TODO create refresher <- change player team when changes the group
    private final XEssentials xEssentials;
    private final Scoreboard scoreboard;
    private final GroupComparator groupComparator;
    private final HashSet<PlayerTeam> teams;
    private final HashMap<Group, String> priorities;
    private final HashMap<Group, Collection<String>> members;

    public TablistSortener(XEssentials xEssentials) {
        this.xEssentials = xEssentials;
        this.groupComparator = new GroupComparator();
        this.scoreboard = ((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle();
        this.teams = new HashSet<>();
        this.priorities = new HashMap<>();
        this.members = new HashMap<>();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (Group group : this.xEssentials.getLuckPerms().getGroupManager().getLoadedGroups()) {
            PlayerTeam playerTeam = this.getTeam(group);
            ((CraftPlayer) event.getPlayer()).getHandle().connection.send(this.createAddTeamPacket(playerTeam));
            Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().connection.send(this.createMembershipPacket(this.getMembers(group), playerTeam)));
        }
    }

    private @NotNull PlayerTeam getTeam(@NotNull Group group) {
        String sortPriority = this.getPriority(group);
        this.priorities.put(group, sortPriority);
        String groupName = group.getName();
        String finalName = sortPriority + groupName;
        if (this.teams.stream().map(playerTeam -> playerTeam.getName()).collect(Collectors.toList()).contains(finalName)) {
            return this.teams.stream().filter(playerTeam -> playerTeam.getName().equals(finalName)).findFirst().get();
        }
        PlayerTeam playerTeam = new PlayerTeam(this.scoreboard, sortPriority + groupName);
        playerTeam.setNameTagVisibility(Team.Visibility.NEVER);
        this.teams.add(playerTeam);
        return playerTeam;
    }

    private @Nullable Group getGroup(Player player) {
        ArrayList<Group> groups = this.xEssentials.getLuckPerms().getGroupManager().getLoadedGroups().stream().collect(Collectors.toCollection(ArrayList::new));
        Collections.sort(groups, this.groupComparator);
        for (Group group : groups) {
            if (player.hasPermission("group." + group.getName())) return group;
        }
        return null;
    }

    private Collection<String> getMembers(Group group) {
        Collection<String> members = Bukkit.getOnlinePlayers().stream()
                .filter(player -> this.getGroup(player).equals(group))
                .map(Player::getName).collect(Collectors.toCollection(ArrayList::new));
        this.members.put(group, members);
        return members;
    }

    private @NotNull String getPriority(@NotNull Group group) {
        String sortPriority = group.getCachedData().getMetaData().getMetaValue("sortPriority");
        if (sortPriority == null || !this.isNumeric(sortPriority) || sortPriority.length() != 4) sortPriority = "9999";

        return sortPriority;
    }

    private boolean isNumeric(@NotNull String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    @Contract("_ -> new")
    private @NotNull ClientboundSetPlayerTeamPacket createAddTeamPacket(PlayerTeam playerTeam) {
        return ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(playerTeam, true);
    }

    @Contract("_ -> new")
    private @NotNull ClientboundSetPlayerTeamPacket createRemoveTeamPacket(PlayerTeam playerTeam) {
        return ClientboundSetPlayerTeamPacket.createRemovePacket(playerTeam);
    }

    @Contract("_, _ -> new")
    private @NotNull ClientboundSetPlayerTeamPacket createMembershipPacket(Collection<String> members, PlayerTeam playerTeam) {
        return ClientboundSetPlayerTeamPacket.createMultiplePlayerPacket(playerTeam, members, ClientboundSetPlayerTeamPacket.Action.ADD);
    }

    @Override
    public void refresh() {
        for (Group group : TablistSortener.this.xEssentials.getLuckPerms().getGroupManager().getLoadedGroups()) {
            this.refreshGroupPriority(group);
            this.refreshPlayerTeam(group);
        }
    }

    private void refreshGroupPriority(Group group) {
        String sortPriority = TablistSortener.this.getPriority(group);
        if (TablistSortener.this.priorities.get(group) == null) TablistSortener.this.priorities.put(group, sortPriority);
        if (!TablistSortener.this.priorities.get(group).equals(sortPriority)) {
            System.out.println(sortPriority + " == " + TablistSortener.this.priorities.get(group));
            String oldName = TablistSortener.this.priorities.get(group) + group.getName();
            Optional<PlayerTeam> oldTeam = TablistSortener.this.teams.stream().filter(playerTeam -> playerTeam.getName().equals(oldName)).findFirst();
            TablistSortener.this.priorities.put(group, sortPriority);
            PlayerTeam newTeam = TablistSortener.this.getTeam(group);
            newTeam.setNameTagVisibility(Team.Visibility.NEVER);
            Bukkit.getOnlinePlayers().forEach(player -> {
                ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
                if (oldTeam.isPresent()) connection.send(TablistSortener.this.createRemoveTeamPacket(oldTeam.get()));
                connection.send(TablistSortener.this.createAddTeamPacket(newTeam));
                connection.send(TablistSortener.this.createMembershipPacket(TablistSortener.this.getMembers(group), newTeam));
            });
        }
    }

    private void refreshPlayerTeam(Group group) {
        if (this.members.get(group) == null) this.members.put(group, this.getMembers(group));
        if (!this.members.get(group).equals(this.getMembers(group))) {
            this.members.put(group, this.getMembers(group));
            Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().connection.send(this.createMembershipPacket(this.getMembers(group), this.getTeam(group))));
        }
    }
}
