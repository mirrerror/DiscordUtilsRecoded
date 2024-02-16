package md.mirrerror.discordutils.events;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class PlayerBanListener implements Listener {

    private final Plugin plugin;
    private final DiscordUtilsBot bot;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(event.getUniqueId());

        if(!discordUtilsUser.isLinked()) return;

        bot.getJda().getGuilds().forEach(guild -> {
            Member member = guild.getMember(discordUtilsUser.getUser());
            if(member == null) return;

            guild.retrieveBan(discordUtilsUser.getUser()).queue(
                    success -> guild.unban(discordUtilsUser.getUser()).queue(),
                    failure -> {
                        if(Bukkit.getBannedPlayers().contains(Bukkit.getOfflinePlayer(event.getUniqueId())))
                            if(guild.getSelfMember().canInteract(member))
                                member.ban(0, TimeUnit.DAYS).queue();
                            else
                                plugin.getLogger().warning("Couldn't ban " + member.getUser().getName() + " because of the hierarchy.");
                    }
            );

        });
    }

}
