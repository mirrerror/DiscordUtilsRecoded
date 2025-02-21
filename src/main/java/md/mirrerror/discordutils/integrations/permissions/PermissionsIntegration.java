package md.mirrerror.discordutils.integrations.permissions;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PermissionsIntegration {

    CompletableFuture<List<String>> getUserGroups(Player player);
    CompletableFuture<String> getHighestUserGroup(Player player);
    CompletableFuture<List<String>> getUserGroups(OfflinePlayer offlinePlayer);
    CompletableFuture<String> getHighestUserGroup(OfflinePlayer offlinePlayer);
    CompletableFuture<Void> assignGroup(Player player, String group);
    CompletableFuture<Void> assignGroup(OfflinePlayer offlinePlayer, String group);
    CompletableFuture<Void> removeGroup(Player player, String group);
    CompletableFuture<Void> removeGroup(OfflinePlayer offlinePlayer, String group);

}
