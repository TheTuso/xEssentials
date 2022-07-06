package pl.tuso.essentials.proxy;

import org.jetbrains.annotations.NotNull;
import pl.tuso.essentials.XEssentials;

import java.util.Collection;
import java.util.UUID;

public class ProxyInfo {
    private final XEssentials xEssentials;
    private final AllPlayersPuller allPlayersPuller;
    private final UniqueIdPuller uniqueIdPuller;

    public ProxyInfo(@NotNull XEssentials xEssentials) {
        this.xEssentials = xEssentials;
        this.allPlayersPuller = new AllPlayersPuller(xEssentials.getCore());
        this.uniqueIdPuller = new UniqueIdPuller(xEssentials.getCore());

        this.xEssentials.getCore().getMessagingService().registerListener(allPlayersPuller);
        this.xEssentials.getCore().getMessagingService().registerListener(uniqueIdPuller);
    }

    public Collection<String> getAllPlayers() {
        return this.allPlayersPuller.pull();
    }

    public Collection<String> getAllPlayers(String server) {
        return this.allPlayersPuller.pull(server);
    }

    public UUID getUuid(String username) {
        return this.uniqueIdPuller.pull(username);
    }
}
