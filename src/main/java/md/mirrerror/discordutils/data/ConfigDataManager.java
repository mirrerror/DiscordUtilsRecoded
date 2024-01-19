package md.mirrerror.discordutils.data;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.config.customconfigs.DataConfig;
import md.mirrerror.discordutils.models.DiscordUtilsUser;

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
            dataConfig.getFileConfiguration().set("DiscordLink." + uuid + ".UserID", userId);
            dataConfig.getFileConfiguration().set("DiscordLink." + uuid + ".2FA", secondFactor);
            dataConfig.getFileConfiguration().set("DiscordLink." + uuid + ".LastBoostingTime", "");
            dataConfig.saveConfigFile();
        });
    }

    @Override
    public CompletableFuture<Void> unregisterUser(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            dataConfig.getFileConfiguration().set("DiscordLink." + uuid, null);
            dataConfig.saveConfigFile();
        });
    }

    @Override
    public CompletableFuture<Boolean> userExists(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> dataConfig.getFileConfiguration().getConfigurationSection("DiscordLink").getKeys(false).contains(uuid.toString()));
    }

    @Override
    public CompletableFuture<Boolean> userLinked(long userId) {
        return CompletableFuture.supplyAsync(() -> {
            for(String entry : dataConfig.getFileConfiguration().getConfigurationSection("DiscordLink").getKeys(false))
                if(dataConfig.getFileConfiguration().getLong("DiscordLink." + entry + ".UserID") == userId) return true;
            return false;
        });
    }

    @Override
    public CompletableFuture<UUID> getPlayerUniqueId(long userId) {
        return CompletableFuture.supplyAsync(() -> {
            for(String entry : dataConfig.getFileConfiguration().getConfigurationSection("DiscordLink").getKeys(false))
                if(dataConfig.getFileConfiguration().getLong("DiscordLink." + entry + ".UserID") == userId) return UUID.fromString(entry);
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> setSecondFactor(UUID uuid, boolean secondFactor) {
        return CompletableFuture.runAsync(() -> {
            dataConfig.getFileConfiguration().set("DiscordLink." + uuid + ".2FA", secondFactor);
            dataConfig.saveConfigFile();
        });
    }

    @Override
    public CompletableFuture<Boolean> hasSecondFactor(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> dataConfig.getFileConfiguration().getBoolean("DiscordLink." + uuid + ".2FA"));
    }

    @Override
    public CompletableFuture<Void> setDiscordUserId(UUID uuid, long userId) {
        return CompletableFuture.runAsync(() -> {
            dataConfig.getFileConfiguration().set("DiscordLink." + uuid + ".UserID", userId);
            dataConfig.saveConfigFile();
        });
    }

    @Override
    public CompletableFuture<Long> getDiscordUserId(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> dataConfig.getFileConfiguration().getLong("DiscordLink." + uuid + ".UserID"));
    }

    @Override
    public CompletableFuture<Void> setLastBoostingTime(UUID uuid, OffsetDateTime lastBoostingTime) {
        return CompletableFuture.runAsync(() -> {
            dataConfig.getFileConfiguration().set("DiscordLink." + uuid + ".LastBoostingTime", lastBoostingTime.toString());
            dataConfig.saveConfigFile();
        });
    }

    @Override
    public CompletableFuture<OffsetDateTime> getLastBoostingTime(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            OffsetDateTime lastBoostingTime;

            String rawBoostingTime = dataConfig.getFileConfiguration().getString("DiscordLink." + uuid + ".LastBoostingTime");
            if(rawBoostingTime == null) {
                dataConfig.getFileConfiguration().set("DiscordLink." + uuid + ".LastBoostingTime", "");
                dataConfig.saveConfigFile();
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
            for(String entry : dataConfig.getFileConfiguration().getConfigurationSection("DiscordLink").getKeys(false))
                if(dataConfig.getFileConfiguration().getLong("DiscordLink." + entry + ".UserID") > 0) count += 1;
            return count;
        });
    }

    @Override
    public CompletableFuture<Void> performUserBatchUpdate(List<UserBatchUpdateEntry> newUsers) {
        return CompletableFuture.runAsync(() -> {
            for(UserBatchUpdateEntry entry : newUsers) {
                dataConfig.getFileConfiguration().set("DiscordLink." + entry.getUuid() + ".UserID", entry.getUserId());
                dataConfig.getFileConfiguration().set("DiscordLink." + entry.getUuid() + ".2FA", entry.isSecondFactorEnabled());
                dataConfig.getFileConfiguration().set("DiscordLink." + entry.getUuid() + ".LastBoostingTime", entry.getLastTimeBoosted());
            }

            dataConfig.saveConfigFile();
        });
    }

    @Override
    public CompletableFuture<List<UserBatchUpdateEntry>> getAllUserBatchEntries() {
        return CompletableFuture.supplyAsync(() -> {

            List<UserBatchUpdateEntry> batchUpdateEntries = new LinkedList<>();

            for(String entry : dataConfig.getFileConfiguration().getConfigurationSection("DiscordLink").getKeys(false)) {
                UUID uuid;

                try {
                    uuid = UUID.fromString(entry);
                } catch (IllegalArgumentException ignored) {
                    continue;
                }

                long userId = dataConfig.getFileConfiguration().getLong("DiscordLink." + entry + ".UserID");
                boolean isSecondFactorEnabled = dataConfig.getFileConfiguration().getBoolean("DiscordLink." + entry + ".2FA");

                OffsetDateTime lastBoostingTime;

                String rawBoostingTime = dataConfig.getFileConfiguration().getString("DiscordLink." + uuid + ".LastBoostingTime");
                if(rawBoostingTime == null) {
                    dataConfig.getFileConfiguration().set("DiscordLink." + uuid + ".LastBoostingTime", "");
                    dataConfig.saveConfigFile();
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
