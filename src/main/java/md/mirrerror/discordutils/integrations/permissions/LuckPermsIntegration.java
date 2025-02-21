package md.mirrerror.discordutils.integrations.permissions;

import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class LuckPermsIntegration implements PermissionsIntegration {

    private final Plugin plugin;
    private final LuckPerms api = LuckPermsProvider.get();

    @Override
    public CompletableFuture<List<String>> getUserGroups(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getUserGroups(loadUser(player.getUniqueId()));
            } catch (IllegalStateException ignored) {
                plugin.getLogger().severe("Something went wrong while using the LuckPerms integration. Probably, there is not the LuckPerms plugin installed on your server.");
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<String> getHighestUserGroup(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = loadUser(player.getUniqueId());
                if(user != null) return user.getPrimaryGroup();
            } catch (IllegalStateException ignored) {
                plugin.getLogger().severe("Something went wrong while using the LuckPerms integration. Probably, there is not the LuckPerms plugin installed on your server.");
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<List<String>> getUserGroups(OfflinePlayer offlinePlayer) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getUserGroups(loadUser(offlinePlayer.getUniqueId()));
            } catch (IllegalStateException ignored) {
                plugin.getLogger().severe("Something went wrong while using the LuckPerms integration. Probably, there is not the LuckPerms plugin installed on your server.");
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<String> getHighestUserGroup(OfflinePlayer offlinePlayer) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = loadUser(offlinePlayer.getUniqueId());
                if(user != null) return user.getPrimaryGroup();
            } catch (IllegalStateException ignored) {
                plugin.getLogger().severe("Something went wrong while using the LuckPerms integration. Probably, there is not the LuckPerms plugin installed on your server.");
            }

            return null;
        });
    }

    @Override
    public CompletableFuture<Void> assignGroup(Player player, String group) {
        return CompletableFuture.runAsync(() -> assignGroup(player.getUniqueId(), group));
    }

    @Override
    public CompletableFuture<Void> assignGroup(OfflinePlayer offlinePlayer, String group) {
        return CompletableFuture.runAsync(() -> assignGroup(offlinePlayer.getUniqueId(), group));
    }

    @Override
    public CompletableFuture<Void> removeGroup(Player player, String group) {
        return CompletableFuture.runAsync(() -> removeGroup(player.getUniqueId(), group));
    }

    @Override
    public CompletableFuture<Void> removeGroup(OfflinePlayer offlinePlayer, String group) {
        return CompletableFuture.runAsync(() -> removeGroup(offlinePlayer.getUniqueId(), group));
    }

    private User loadUser(UUID uuid) {
        User user = api.getUserManager().getUser(uuid);
        if(user == null) user = api.getUserManager().loadUser(uuid).join();

        return user;
    }

    private List<String> getUserGroups(User user) {
        List<String> groups = new ArrayList<>();

        if(user != null) {
            Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
            for(Group group : inheritedGroups) {
                groups.add(group.getName());
            }
        }

        return groups;
    }

    private void assignGroup(UUID uuid, String group) {
        try {
            User user = loadUser(uuid);

            if (user != null) {
                user.data().add(InheritanceNode.builder(group).build());
                api.getUserManager().saveUser(user);
            }
        } catch (IllegalStateException ignored) {
            plugin.getLogger().severe("Something went wrong while using the LuckPerms integration. Probably, there is not the LuckPerms plugin installed on your server.");
        }
    }

    private void removeGroup(UUID uuid, String group) {
        try {
            User user = loadUser(uuid);

            if (user != null) {
                user.data().remove(InheritanceNode.builder(group).build());
                api.getUserManager().saveUser(user);
            }
        } catch (IllegalStateException ignored) {
            plugin.getLogger().severe("Something went wrong while using the LuckPerms integration. Probably, there is not the LuckPerms plugin installed on your server.");
        }
    }

}
