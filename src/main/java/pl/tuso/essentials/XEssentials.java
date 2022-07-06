package pl.tuso.essentials;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tuso.core.XCore;
import pl.tuso.essentials.chat.ChatListener;
import pl.tuso.essentials.config.Configuration;
import pl.tuso.essentials.config.ReloadCommand;
import pl.tuso.essentials.direct.DirectMessageManager;
import pl.tuso.essentials.hub.HubCommand;
import pl.tuso.essentials.nametag.Nametag;
import pl.tuso.essentials.nametag.NametagBehavior;
import pl.tuso.essentials.packet.PacketPlayerInjector;
import pl.tuso.essentials.proxy.ProxyInfo;
import pl.tuso.essentials.spawn.SpawnOnJoin;
import pl.tuso.essentials.tablist.*;
import pl.tuso.essentials.team.TeamPacketFixer;

public class XEssentials extends JavaPlugin {
    private Configuration configuration;
    private XCore xCore;
    private LuckPerms luckPerms;
    private ProxyInfo proxyInfo;

    @Override
    public void onEnable() {
        this.configuration = new Configuration(this);
        this.xCore = this.getServer().getPluginManager().isPluginEnabled("xCore") ? XCore.getInstance() : null;
        this.luckPerms = this.getServer().getPluginManager().isPluginEnabled("luckperms") ? LuckPermsProvider.get() : null;
        this.proxyInfo = new ProxyInfo(this);

        Nametag.registerEntityType();

        this.registerCommands();
        this.registerEvents();

        this.getLogger().info("Hi!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Bayo!");
    }

    private void registerCommands() {
        new DirectMessageManager(this);
        new ReloadCommand(this).register(this.getCommand("xreload"));
        new HubCommand(this).register(this.getCommand("hub"));
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new PacketPlayerInjector(), this);
        this.getServer().getPluginManager().registerEvents(new TeamPacketFixer(), this);
        this.getServer().getPluginManager().registerEvents(new NametagBehavior(this), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        this.getServer().getPluginManager().registerEvents(new FakePlayerInjector(), this);
        this.getServer().getPluginManager().registerEvents(new SpawnOnJoin(this), this);

        TablistContentInjector tablistContentInjector = new TablistContentInjector(this);
        TablistSortener tablistSortener = new TablistSortener(this);
        TablistPlayerName tablistPlayerName = new TablistPlayerName(this);
        this.getServer().getPluginManager().registerEvents(tablistContentInjector, this);
        this.getServer().getPluginManager().registerEvents(tablistSortener, this);
        this.getServer().getPluginManager().registerEvents(tablistPlayerName, this);
        new TablistRefresher(this, tablistSortener, tablistContentInjector, tablistPlayerName);
    }

//    public static XEssentials getInstance() {
//        return instance;
//    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public ProxyInfo getProxyInfo() {
        return this.proxyInfo;
    }

    public XCore getCore() {
        return this.xCore;
    }

    public LuckPerms getLuckPerms() {
        return this.luckPerms;
    }
}