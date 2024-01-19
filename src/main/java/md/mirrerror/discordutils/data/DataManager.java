package md.mirrerror.discordutils.data;

import java.time.OffsetDateTime;
import java.util.List;
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

    CompletableFuture<Void> setLastBoostingTime(UUID uuid, OffsetDateTime lastBoostingTime);
    CompletableFuture<OffsetDateTime> getLastBoostingTime(UUID uuid);

    CompletableFuture<Long> countLinkedUsers();
    CompletableFuture<Void> performUserBatchUpdate(List<UserBatchUpdateEntry> newUsers);
    CompletableFuture<List<UserBatchUpdateEntry>> getAllUserBatchEntries();

}
