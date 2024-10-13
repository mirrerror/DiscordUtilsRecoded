package md.mirrerror.discordutils.data;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.config.customconfigs.DataConfig;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ConfigDataManager implements DataManager {

    private final DataConfig dataConfig;

    @Override
    public CompletableFuture<Void> setup() {
        return CompletableFuture.runAsync(() -> {}); // Config files are already initialized on the start
    }

    @Override
    public CompletableFuture<Void> registerUser(UUID uuid, long userId, boolean secondFactor) {
        return CompletableFuture.runAsync(() -> {
            dataConfig.getConfig().set("DiscordLink." + uuid + ".UserID", userId);
            dataConfig.getConfig().set("DiscordLink." + uuid + ".2FA", secondFactor);
            dataConfig.getConfig().set("DiscordLink." + uuid + ".LastBoostingTime", "");
        });
    }

    @Override
    public CompletableFuture<Void> unregisterUser(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            dataConfig.getConfig().set("DiscordLink." + uuid, null);
        });
    }

    @Override
    public CompletableFuture<Boolean> userExists(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> dataConfig.getConfig().getSection("DiscordLink").singleLayerKeySet().contains(uuid.toString()));
    }

    @Override
    public CompletableFuture<Boolean> userLinked(long userId) {
        return CompletableFuture.supplyAsync(() -> {
            for(String entry : dataConfig.getConfig().getSection("DiscordLink").singleLayerKeySet())
                if(dataConfig.getConfig().getLong("DiscordLink." + entry + ".UserID") == userId) return true;
            return false;
        });
    }

    @Override
    public CompletableFuture<UUID> getPlayerUniqueId(long userId) {
        return CompletableFuture.supplyAsync(() -> {
            for(String entry : dataConfig.getConfig().getSection("DiscordLink").singleLayerKeySet())
                if(dataConfig.getConfig().getLong("DiscordLink." + entry + ".UserID") == userId) return UUID.fromString(entry);
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> setSecondFactor(UUID uuid, boolean secondFactor) {
        return CompletableFuture.runAsync(() -> {
            dataConfig.getConfig().set("DiscordLink." + uuid + ".2FA", secondFactor);
        });
    }

    @Override
    public CompletableFuture<Boolean> hasSecondFactor(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> dataConfig.getConfig().getBoolean("DiscordLink." + uuid + ".2FA"));
    }

    @Override
    public CompletableFuture<Void> setDiscordUserId(UUID uuid, long userId) {
        return CompletableFuture.runAsync(() -> {
            dataConfig.getConfig().set("DiscordLink." + uuid + ".UserID", userId);
        });
    }

    @Override
    public CompletableFuture<Long> getDiscordUserId(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> dataConfig.getConfig().getLong("DiscordLink." + uuid + ".UserID"));
    }

    @Override
    public CompletableFuture<Void> setLastBoostingTime(UUID uuid, OffsetDateTime lastBoostingTime) {
        return CompletableFuture.runAsync(() -> {
            dataConfig.getConfig().set("DiscordLink." + uuid + ".LastBoostingTime", lastBoostingTime.toString());
        });
    }

    @Override
    public CompletableFuture<OffsetDateTime> getLastBoostingTime(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            OffsetDateTime lastBoostingTime;

            String rawBoostingTime = dataConfig.getConfig().getString("DiscordLink." + uuid + ".LastBoostingTime");
            if(rawBoostingTime == null) {
                dataConfig.getConfig().set("DiscordLink." + uuid + ".LastBoostingTime", "");
                return null;
            }

            try {
                lastBoostingTime = OffsetDateTime.parse(rawBoostingTime);
            } catch (DateTimeParseException ignored) {
                return null;
            }

            return lastBoostingTime;
        });
    }

    @Override
    public CompletableFuture<Long> countLinkedUsers() {
        return CompletableFuture.supplyAsync(() -> {
            long count = 0;
            for(String entry : dataConfig.getConfig().getSection("DiscordLink").singleLayerKeySet())
                if(dataConfig.getConfig().getLong("DiscordLink." + entry + ".UserID") > 0) count += 1;
            return count;
        });
    }

    @Override
    public CompletableFuture<Void> performUserBatchUpdate(List<UserBatchUpdateEntry> newUsers) {
        return CompletableFuture.runAsync(() -> {
            for(UserBatchUpdateEntry entry : newUsers) {
                dataConfig.getConfig().set("DiscordLink." + entry.getUuid() + ".UserID", entry.getUserId());
                dataConfig.getConfig().set("DiscordLink." + entry.getUuid() + ".2FA", entry.isSecondFactorEnabled());
                dataConfig.getConfig().set("DiscordLink." + entry.getUuid() + ".LastBoostingTime", entry.getLastTimeBoosted());
            }
        });
    }

    @Override
    public CompletableFuture<List<UserBatchUpdateEntry>> getAllUserBatchEntries() {
        return CompletableFuture.supplyAsync(() -> {

            List<UserBatchUpdateEntry> batchUpdateEntries = new LinkedList<>();

            for(String entry : dataConfig.getConfig().getSection("DiscordLink").singleLayerKeySet()) {
                UUID uuid;

                try {
                    uuid = UUID.fromString(entry);
                } catch (IllegalArgumentException ignored) {
                    continue;
                }

                long userId = dataConfig.getConfig().getLong("DiscordLink." + entry + ".UserID");
                boolean isSecondFactorEnabled = dataConfig.getConfig().getBoolean("DiscordLink." + entry + ".2FA");

                OffsetDateTime lastBoostingTime;

                String rawBoostingTime = dataConfig.getConfig().getString("DiscordLink." + uuid + ".LastBoostingTime");
                if(rawBoostingTime == null) {
                    dataConfig.getConfig().set("DiscordLink." + uuid + ".LastBoostingTime", "");
                    return null;
                }

                try {
                    lastBoostingTime = OffsetDateTime.parse(rawBoostingTime);
                } catch (DateTimeParseException ignored) {
                    return null;
                }

                batchUpdateEntries.add(new UserBatchUpdateEntry(uuid, userId, isSecondFactorEnabled, lastBoostingTime));
            }

            return batchUpdateEntries;

        });
    }
}
