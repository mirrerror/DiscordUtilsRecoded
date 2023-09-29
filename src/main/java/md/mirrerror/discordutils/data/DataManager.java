package md.mirrerror.discordutils.data;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DataManager {

    CompletableFuture<Void> setup();

    CompletableFuture<Void> registerUser(UUID uuid, long userId, boolean secondFactor);
    CompletableFuture<Void> unregisterUser(UUID uuid);
    CompletableFuture<Boolean> userExists(UUID uuid);

    CompletableFuture<Boolean> userLinked(long userId);
    CompletableFuture<UUID> getPlayerUniqueId(long userId);

    CompletableFuture<Void> setSecondFactor(UUID uuid, boolean secondFactor);
    CompletableFuture<Boolean> hasSecondFactor(UUID uuid);

    CompletableFuture<Void> setDiscordUserId(UUID uuid, long userId);
    CompletableFuture<Long> getDiscordUserId(UUID uuid);

    CompletableFuture<Long> countLinkedUsers();

}
