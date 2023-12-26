package md.mirrerror.discordutils.integrations.placeholders;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Getter
public class PAPIManager {

    private final Plugin plugin;

    private final boolean isEnabled;

    public PAPIManager(Plugin plugin) {
        this.plugin = plugin;
        this.isEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if(!isEnabled) {
            plugin.getLogger().warning("It seems like you don't have PlaceholderAPI installed on your server. Disabling PAPIManager...");
        } else {
            new PAPIExpansion(plugin).register();
            plugin.getLogger().info("PAPIManager has been successfully enabled.");
        }
    }

    public String setPlaceholders(Player player, String s) {
        if(isEnabled) return PlaceholderAPI.setPlaceholders(player, s);
        return s;
    }

    public String setPlaceholders(OfflinePlayer offlinePlayer, String s) {
        if(isEnabled) return PlaceholderAPI.setPlaceholders(offlinePlayer, s);
        return s;
    }

}
