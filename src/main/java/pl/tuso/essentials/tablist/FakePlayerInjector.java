package pl.tuso.essentials.tablist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class FakePlayerInjector implements Listener {
    private final FakePlayerGenerator fakePlayerGenerator;

    public FakePlayerInjector() {
        this.fakePlayerGenerator = new FakePlayerGenerator();
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        this.fakePlayerGenerator.loadFakePlayers(event.getPlayer());
    }
}
