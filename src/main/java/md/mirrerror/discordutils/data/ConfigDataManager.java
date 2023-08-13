package md.mirrerror.discordutils.data;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.customconfigs.DataConfig;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ConfigDataManager implements DataManager {
    private DataConfig dataConfig = Main.getInstance().getConfigManager().getData();

    @Override
    public CompletableFuture<Void> setup() {
        return CompletableFuture.runAsync(() -> {}); // Config files are already initialized on the start
    }

    @Override
    public CompletableFuture<Void> registerUser(UUID uuid, long userId, boolean secondFactor) {
        return CompletableFuture.runAsync(() -> {
            dataConfig.getFileConfiguration().set("DiscordLink." + uuid + ".UserID", userId);
            dataConfig.getFileConfiguration().set("DiscordLink." + uuid + ".2FA", secondFactor);
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
    public CompletableFuture<Long> countLinkedUsers() {
        return CompletableFuture.supplyAsync(() -> {
            long count = 0;
            for(String entry : dataConfig.getFileConfiguration().getConfigurationSection("DiscordLink").getKeys(false))
                if(dataConfig.getFileConfiguration().getLong("DiscordLink." + entry + ".UserID") > 0) count += 1;
            return count;
        });
    }
}
