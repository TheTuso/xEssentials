package pl.tuso.essentials.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import pl.tuso.essentials.XEssentials;

public class Configuration {
    private final XEssentials xEssentials;
    private final MiniMessage miniMessage;

    public Configuration(@NotNull XEssentials xEssentials) {
        this.xEssentials = xEssentials;
        this.miniMessage = MiniMessage.miniMessage();
        xEssentials.saveDefaultConfig();
    }

    public Component getPlayerListHeader() {
        String header = this.xEssentials.getConfig().getString("playerlist.header");
        return this.miniMessage.deserialize(header);
    }

    public Component getPlayerListFooter() {
        String footer = this.xEssentials.getConfig().getString("playerlist.footer");
        String replaced = footer.replace("${SERVER_NAME}", this.xEssentials.getCore().getServerInfo().getName());
        return this.miniMessage.deserialize(replaced);
    }

    public boolean shouldSpawnOnJoin() {
        return this.xEssentials.getConfig().getBoolean("spawn-on-join");
    }

    public void reload() {
        this.xEssentials.reloadConfig();
        this.xEssentials.saveDefaultConfig();
        this.xEssentials.getConfig().options().copyDefaults(true);
        this.xEssentials.saveConfig();
    }
}
