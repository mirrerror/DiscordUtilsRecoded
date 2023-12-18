package md.mirrerror.discordutils.events;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CacheListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!Main.getInstance().isMainReady() || !Main.getInstance().isBotReady()) {
            player.kickPlayer(Message.PLUGIN_IS_NOT_READY_YET.getText());
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());
            for(Guild guild : Main.getInstance().getBot().getJda().getGuilds()) {
                discordUtilsUser.synchronizeRoles(guild);
                discordUtilsUser.synchronizeNickname(guild);
            }
        });
    }

}
