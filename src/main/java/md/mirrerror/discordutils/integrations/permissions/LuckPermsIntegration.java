package md.mirrerror.discordutils.integrations.permissions;

import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

@RequiredArgsConstructor
public class LuckPermsIntegration implements PermissionsIntegration {

    private final Plugin plugin;

    @Override
    public List<String> getUserGroups(Player player) {
        List<String> groups = new ArrayList<>();
        try {
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().getUser(player.getUniqueId());
            if(user == null) user = api.getUserManager().loadUser(player.getUniqueId()).join();

            if(user != null) {
                Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
                for(Group group : inheritedGroups) {
                    groups.add(group.getName());
                }
            }
        } catch (IllegalStateException ignored) {
            plugin.getLogger().severe("Something went wrong while using the LuckPerms integration. Probably, there is not the LuckPerms plugin installed on your server.");
        }
        return groups;
    }

    @Override
    public String getHighestUserGroup(Player player) {
        try {
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().getUser(player.getUniqueId());
            if(user == null) user = api.getUserManager().loadUser(player.getUniqueId()).join();

            if(user != null) return user.getPrimaryGroup();
        } catch (IllegalStateException ignored) {
            plugin.getLogger().severe("Something went wrong while using the LuckPerms integration. Probably, there is not the LuckPerms plugin installed on your server.");
        }

        return null;
    }

    @Override
    public List<String> getUserGroups(OfflinePlayer offlinePlayer) {
        List<String> groups = new ArrayList<>();
        try {
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().getUser(offlinePlayer.getUniqueId());
            if(user == null) user = api.getUserManager().loadUser(offlinePlayer.getUniqueId()).join();

            if(user != null) {
                Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
                for(Group group : inheritedGroups) {
                    groups.add(group.getName());
                }
            }
        } catch (IllegalStateException ignored) {
            plugin.getLogger().severe("Something went wrong while using the LuckPerms integration. Probably, there is not the LuckPerms plugin installed on your server.");
        }
        return groups;
    }

    @Override
    public String getHighestUserGroup(OfflinePlayer offlinePlayer) {
        try {
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getUserManager().getUser(offlinePlayer.getUniqueId());
            if(user == null) user = api.getUserManager().loadUser(offlinePlayer.getUniqueId()).join();

            if(user != null) return user.getPrimaryGroup();
        } catch (IllegalStateException ignored) {
            plugin.getLogger().severe("Something went wrong while using the LuckPerms integration. Probably, there is not the LuckPerms plugin installed on your server.");
        }

        return null;
    }

}
