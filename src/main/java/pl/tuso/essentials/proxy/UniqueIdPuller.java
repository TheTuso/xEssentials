package pl.tuso.essentials.proxy;

import org.jetbrains.annotations.NotNull;
import pl.tuso.core.XCore;
import pl.tuso.core.lettuce.messaging.Message;
import pl.tuso.core.lettuce.messaging.MessagingListener;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UniqueIdPuller implements MessagingListener {
    private final XCore xCore;
    private CompletableFuture<UUID> future;

    public UniqueIdPuller(XCore xCore) {
        this.xCore = xCore;
        this.future = new CompletableFuture<>();
    }

    @Override
    public void action(@NotNull Message message) {
        if (!message.containsParam("uuid")) return;
        UUID uuid = UUID.fromString(message.getParam("uuid"));
        this.future.complete(uuid);
    }

    @Override
    public @NotNull String getType() {
        return "UNIQUE_ID";
    }

    public UUID pull(String username) {
        this.future = new CompletableFuture<>();
        this.xCore.getMessagingService().sendOutgoingMessage(new Message("UNIQUE_ID").setParam("username", username));
        try {
            return this.future.get(4, TimeUnit.SECONDS);
        } catch (Exception exception) {
            return null;
        }
    }
}
