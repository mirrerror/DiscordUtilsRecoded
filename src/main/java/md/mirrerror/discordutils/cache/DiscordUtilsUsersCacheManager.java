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

    private static Set<DiscordUtilsUser> cachedUsers;

    public static void initialize() {
        cachedUsers = new HashSet<>();
    }

    public static DiscordUtilsUser retrieveUserFromDatabaseByUuid(UUID uuid) {
        /*AtomicReference<DiscordUtilsUser> discordUtilsUser = new AtomicReference<>();

        Main.getInstance().getDataManager().userExists(uuid).whenComplete((exists, throwable2) -> {
            if(throwable2 != null) {
                Main.getInstance().getLogger().severe("Something went wrong while checking the existence of the user for the UUID: " + uuid + ".");
                return;
            }

            if(!exists) {
                try {
                    Main.getInstance().getDataManager().registerUser(uuid, -1, false).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            Main.getInstance().getDataManager().getDiscordUserId(uuid).whenComplete((id, throwable) -> {
                if(throwable != null) {
                    Main.getInstance().getLogger().severe("Something went wrong while retrieving the user ID for the UUID: " + uuid + ".");
                    return;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                User user = Main.getInstance().getBot().getJda().getUserById(id);

                Main.getInstance().getDataManager().hasSecondFactor(offlinePlayer.getUniqueId()).whenComplete((result, throwable1) -> {
                    if(throwable1 != null) {
                        Main.getInstance().getLogger().severe("Something went wrong while retrieving the player's 2FA status for the user: " + user.getIdLong() + ".");
                        return;
                    }

                    discordUtilsUser.set(new DiscordUtilsUser(offlinePlayer, user, result));
                    Main.getInstance().getLogger().info("discordutilsuser1: " + discordUtilsUser.get());
                });
            });
        });

        Main.getInstance().getLogger().info("discordutilsuser2: " + discordUtilsUser.get());

        addToCache(discordUtilsUser.get());
        return discordUtilsUser.get();*/

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
            DiscordUtilsUser discordUtilsUser = new DiscordUtilsUser(offlinePlayer,
                    Main.getInstance().getBot().getJda().getUserById(Main.getInstance().getDataManager().getDiscordUserId(uuid).get()),
                    Main.getInstance().getDataManager().hasSecondFactor(offlinePlayer.getUniqueId()).get());
            addToCache(discordUtilsUser);
            return discordUtilsUser;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static DiscordUtilsUser retrieveUserFromDatabaseByUserId(long userId) {
        /*AtomicReference<DiscordUtilsUser> discordUtilsUser = new AtomicReference<>();

        Main.getInstance().getDataManager().getPlayerUniqueId(userId).whenComplete((uuid, throwable) -> {
            if(throwable != null) {
                Main.getInstance().getLogger().severe("Something went wrong while retrieving the player's UUID for the user: " + userId + ".");
                return;
            }

            if(uuid == null) {
                //Main.getInstance().getLogger().severe("The plugin is trying to retrieve a plugin user that doesn't exist from the database (user ID: " + userId + ")!");
                return;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            User user = Main.getInstance().getBot().getJda().getUserById(userId);

            Main.getInstance().getDataManager().hasSecondFactor(uuid).whenComplete((result, throwable1) -> {
                if(throwable1 != null) {
                    Main.getInstance().getLogger().severe("Something went wrong while retrieving the player's 2FA status for the user: " + userId + ".");
                    return;
                }
                discordUtilsUser.set(new DiscordUtilsUser(offlinePlayer, user, result));
            });
        });

        addToCache(discordUtilsUser.get());
        return discordUtilsUser.get();*/

        try {
            UUID uuid = Main.getInstance().getDataManager().getPlayerUniqueId(userId).get();
            if(uuid == null) return new DiscordUtilsUser(null, null, false);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            DiscordUtilsUser discordUtilsUser = new DiscordUtilsUser(offlinePlayer, Main.getInstance().getBot().getJda().getUserById(userId),
                    Main.getInstance().getDataManager().hasSecondFactor(uuid).get());
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
        System.out.println("before remove: " + cachedUsers.size());
        cachedUsers.remove(discordUtilsUser);
        System.out.println("after remove: " + cachedUsers.size());
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
