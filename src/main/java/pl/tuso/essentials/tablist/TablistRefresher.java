package pl.tuso.essentials.tablist;

import org.bukkit.scheduler.BukkitRunnable;
import pl.tuso.essentials.XEssentials;
import pl.tuso.essentials.refresh.Refreshable;

public class TablistRefresher {
    private final XEssentials xEssentials;
    private final Refreshable[] refreshables;
    private final int SECONDS;

    public TablistRefresher(XEssentials xEssentials, Refreshable... refreshables) {
        this.xEssentials = xEssentials;
        this.refreshables = refreshables;
        this.SECONDS = 3;
        this.startRefresher();
    }

    private void startRefresher() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Refreshable refreshable : TablistRefresher.this.refreshables) {
                    refreshable.refresh();
                }
            }
        }.runTaskTimer(this.xEssentials, 0, 20 * this.SECONDS);
    }
}
