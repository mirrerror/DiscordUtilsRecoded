package md.mirrerror.discordutils.integrations.permissions;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public interface PermissionsIntegration {

    List<String> getUserGroups(Player player);
    String getHighestUserGroup(Player player);
    List<String> getUserGroups(OfflinePlayer offlinePlayer);
    String getHighestUserGroup(OfflinePlayer offlinePlayer);

}
