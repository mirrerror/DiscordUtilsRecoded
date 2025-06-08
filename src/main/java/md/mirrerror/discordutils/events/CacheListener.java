package md.mirrerror.discordutils.events;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class CacheListener implements Listener {

    private final Main plugin;

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(!Main.isMainReady() || !Main.isBotReady()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Message.PLUGIN_IS_NOT_READY_YET.getText());
            return;
        }

        DiscordUtilsUsersCacheManager.getFromCacheByUuid(event.getUniqueId(), true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId(), true);
            for(Guild guild : plugin.getBot().getJda().getGuilds()) {
                discordUtilsUser.synchronizeRoles(guild);
                discordUtilsUser.synchronizeNickname(guild);
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> DiscordUtilsUsersCacheManager.removeFromCacheByUuid(player.getUniqueId()));
    }

}
