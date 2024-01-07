package md.mirrerror.discordutils.cache;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class DiscordUtilsUsersCacheManager {

    private static final Set<DiscordUtilsUser> cachedUsers = new HashSet<>();

    public static DiscordUtilsUser retrieveUserFromDatabaseByUuid(UUID uuid) {
        try {
            boolean exists = Main.getInstance().getDataManager().userExists(uuid).get();
            if(!exists) {
                try {
                    Main.getInstance().getDataManager().registerUser(uuid, -1, false).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            DiscordUtilsUser discordUtilsUser = new DiscordUtilsUser(Main.getInstance().getBot(), Main.getInstance().getDataManager(), offlinePlayer,
                    Main.getInstance().getBot().getJda().getUserById(Main.getInstance().getDataManager().getDiscordUserId(uuid).get()),
                    Main.getInstance().getDataManager().hasSecondFactor(offlinePlayer.getUniqueId()).get(),
                    Main.getInstance().getDataManager().getLastBoostingTime(offlinePlayer.getUniqueId()).get());
            addToCache(discordUtilsUser);
            return discordUtilsUser;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static DiscordUtilsUser retrieveUserFromDatabaseByUserId(long userId) {
        try {
            UUID uuid = Main.getInstance().getDataManager().getPlayerUniqueId(userId).get();
            if(uuid == null) return new DiscordUtilsUser(null, null, null, null, false, null);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            DiscordUtilsUser discordUtilsUser = new DiscordUtilsUser(Main.getInstance().getBot(), Main.getInstance().getDataManager(),
                    offlinePlayer, Main.getInstance().getBot().getJda().getUserById(userId),
                    Main.getInstance().getDataManager().hasSecondFactor(uuid).get(),
                    Main.getInstance().getDataManager().getLastBoostingTime(offlinePlayer.getUniqueId()).get());
            addToCache(discordUtilsUser);
            return discordUtilsUser;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addToCache(DiscordUtilsUser discordUtilsUser) {
        removeFromCache(discordUtilsUser);
        cachedUsers.add(discordUtilsUser);
    }

    public static void removeFromCache(DiscordUtilsUser discordUtilsUser) {
        cachedUsers.remove(discordUtilsUser);
    }

    public static void removeFromCacheByUuid(UUID uuid) {
        Iterator<DiscordUtilsUser> it = cachedUsers.iterator();
        while (it.hasNext()) {
            DiscordUtilsUser user = it.next();
            try {
                if(user.getOfflinePlayer().getUniqueId().equals(uuid)) {
                    it.remove();
                    break;
                }
            } catch (NullPointerException ignored) {}
        }
    }

    public static void removeFromCacheByUserId(long userId) {
        Iterator<DiscordUtilsUser> it = cachedUsers.iterator();
        while (it.hasNext()) {
            DiscordUtilsUser user = it.next();
            try {
                if(user.getUser().getIdLong() == userId) {
                    it.remove();
                    break;
                }
            } catch (NullPointerException ignored) {}
        }
    }

    public static DiscordUtilsUser getFromCacheByUuid(UUID uuid) {
        for(DiscordUtilsUser discordUtilsUser : cachedUsers) {
            try {
                if(discordUtilsUser.getOfflinePlayer().getUniqueId().equals(uuid)) return discordUtilsUser;
            } catch (NullPointerException ignored) {}
        }
        return retrieveUserFromDatabaseByUuid(uuid);
    }

    public static DiscordUtilsUser getFromCacheByUserId(long userId) {
        for(DiscordUtilsUser discordUtilsUser : cachedUsers) {
            try {
                if(discordUtilsUser.getUser().getIdLong() == userId) return discordUtilsUser;
            } catch (NullPointerException ignored) {}
        }
        return retrieveUserFromDatabaseByUserId(userId);
    }

    public static Set<DiscordUtilsUser> getCachedUsers() {
        return cachedUsers;
    }
}
