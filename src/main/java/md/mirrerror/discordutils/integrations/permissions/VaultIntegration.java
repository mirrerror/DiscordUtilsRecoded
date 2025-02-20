package md.mirrerror.discordutils.integrations.permissions;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.Bukkit.getServer;

public class VaultIntegration implements PermissionsIntegration {

    private static Permission perms = null;

    public VaultIntegration(Plugin plugin) {
        if(!setupPermissions()) {
            plugin.getLogger().severe("Vault plugin or any permissions plugin not found.");
        }
    }

    @Override
    public CompletableFuture<List<String>> getUserGroups(Player player) {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(Arrays.asList(perms.getPlayerGroups(player))));
    }

    @Override
    public CompletableFuture<String> getHighestUserGroup(Player player) {
        return CompletableFuture.supplyAsync(() -> perms.getPrimaryGroup(player));
    }

    @Override
    public CompletableFuture<List<String>> getUserGroups(OfflinePlayer offlinePlayer) {
        return CompletableFuture.supplyAsync(() -> new ArrayList<>(Arrays.asList(perms.getPlayerGroups(null, offlinePlayer))));
    }

    @Override
    public CompletableFuture<String> getHighestUserGroup(OfflinePlayer offlinePlayer) {
        return CompletableFuture.supplyAsync(() -> perms.getPrimaryGroup(null, offlinePlayer));
    }

    @Override
    public CompletableFuture<Void> assignGroup(Player player, String group) {
        return CompletableFuture.runAsync(() -> perms.playerAddGroup(player, group));
    }

    @Override
    public CompletableFuture<Void> assignGroup(OfflinePlayer offlinePlayer, String group) {
        return CompletableFuture.runAsync(() -> perms.playerAddGroup(null, offlinePlayer, group));
    }

    @Override
    public CompletableFuture<Void> removeGroup(Player player, String group) {
        return CompletableFuture.runAsync(() -> perms.playerRemoveGroup(player, group));
    }

    @Override
    public CompletableFuture<Void> removeGroup(OfflinePlayer offlinePlayer, String group) {
        return CompletableFuture.runAsync(() -> perms.playerRemoveGroup(null, offlinePlayer, group));
    }

    public boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
}
