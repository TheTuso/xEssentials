package pl.tuso.essentials.proxy;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import pl.tuso.core.XCore;
import pl.tuso.core.lettuce.messaging.Message;
import pl.tuso.core.lettuce.messaging.MessagingListener;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AllPlayersPuller implements MessagingListener {
    private static XCore xCore;
    private final Gson gson;
    private CompletableFuture<Collection<String>> future;

    public AllPlayersPuller(XCore xCore) {
        this.xCore = xCore;
        this.gson = new Gson();
        this.future = new CompletableFuture<>();
    }

    @Override
    public void action(@NotNull Message message) {
        if (!message.containsParam("players")) return;
        Collection<String> players = this.gson.fromJson(message.getParam("players"), Collection.class);
        this.future.complete(players);
    }

    @Override
    public @NotNull String getType() {
        return "ALL_PLAYERS";
    }

    public Collection<String> pull() {
        this.future = new CompletableFuture<>();
        this.xCore.getMessagingService().sendOutgoingMessage(new Message("ALL_PLAYERS"));
        try {
            return this.future.get(4, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            return null;
        }
    }

    public Collection<String> pull(String server) {
        this.xCore.getMessagingService().sendOutgoingMessage(new Message("ALL_PLAYERS").setParam("server", server));
        try {
            return this.future.get(4, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            return null;
        }
    }
}
